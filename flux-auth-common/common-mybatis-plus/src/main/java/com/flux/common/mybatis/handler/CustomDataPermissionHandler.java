package com.flux.common.mybatis.handler;

import com.baomidou.mybatisplus.extension.plugins.handler.MultiDataPermissionHandler;
import com.flux.common.mybatis.annotation.DataPermission;
import com.flux.common.mybatis.model.UserDataScope;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName CustomDataPermissionHandler
 * @Author njq
 * @Date 2026/10/28 15:28
 * @Description 数据权限处理器：只对加了 @DataPermission 的 mapper 方法生效，
 * 具体条件由 {@link DataPermissionSqlBuilder} 按数据范围拼出来
 * 参考文档：https://baomidou.com/plugins/data-permission/
 */
@Slf4j
public class CustomDataPermissionHandler implements MultiDataPermissionHandler {

    /**
     * mappedStatementId -> 注解，反射开销只付一次
     */
    private final Map<String, Optional<DataPermission>> annotationCache = new ConcurrentHashMap<>();

    private final ObjectProvider<DataPermissionProvider> provider;

    public CustomDataPermissionHandler(ObjectProvider<DataPermissionProvider> provider) {
        this.provider = provider;
    }

    @Override
    public Expression getSqlSegment(Table table, Expression where, String mappedStatementId) {
        DataPermission annotation = findAnnotation(mappedStatementId);
        if (annotation == null) {
            return null;
        }

        DataPermissionProvider dataPermissionProvider = provider.getIfAvailable();
        if (dataPermissionProvider == null) {
            return null;
        }

        UserDataScope scope = dataPermissionProvider.getCurrentScope();
        String sql = DataPermissionSqlBuilder.build(scope, annotation, table);
        if (!StringUtils.hasText(sql)) {
            return null;
        }

        try {
            return CCJSqlParserUtil.parseCondExpression(sql);
        } catch (JSQLParserException e) {
            // 拼错了就当作不限制，至少不打断业务查询，日志里能看到
            log.error("数据权限条件解析失败: {}, table={}", sql, table.getName(), e);
            return null;
        }
    }

    /**
     * 方法上的注解优先，cached 一份避免每次查询都反射
     */
    private DataPermission findAnnotation(String mappedStatementId) {
        return annotationCache
                .computeIfAbsent(mappedStatementId, id -> Optional.ofNullable(doFindAnnotation(id)))
                .orElse(null);
    }

    private DataPermission doFindAnnotation(String mappedStatementId) {
        int index = mappedStatementId.lastIndexOf('.');
        if (index <= 0) {
            return null;
        }
        String className = mappedStatementId.substring(0, index);
        String methodName = mappedStatementId.substring(index + 1);
        try {
            Class<?> mapperClass = Class.forName(className, false, Thread.currentThread().getContextClassLoader());
            for (Method method : mapperClass.getMethods()) {
                if (!method.getName().equals(methodName)) {
                    continue;
                }
                DataPermission annotation = method.getAnnotation(DataPermission.class);
                if (annotation != null) {
                    return annotation;
                }
            }
        } catch (ClassNotFoundException e) {
            log.debug("找不到 Mapper 类，跳过数据权限: {}", className);
        }
        return null;
    }
}
