package com.flux.common.mybatis.handler;

import com.flux.common.core.base.IBaseEnum;
import com.flux.common.mybatis.annotation.DataPermission;
import com.flux.common.mybatis.enums.DataScopeEnum;
import com.flux.common.mybatis.model.UserDataScope;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.schema.Table;
import org.springframework.util.StringUtils;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ClassName DataPermissionSqlBuilder
 * @Author njq
 * @Date 2026/10/28
 * @Description 按数据范围拼 SQL 条件片段。纯函数，不依赖 Spring，方便单测
 */
@Slf4j
public final class DataPermissionSqlBuilder {

    private DataPermissionSqlBuilder() {
    }

    /**
     * @param scope      当前登录人的数据范围，null 表示不限制
     * @param annotation mapper 方法上的 @DataPermission
     * @param table      SQL 里正在处理的表
     * @return 需要 AND 到 where 后面的条件，null 表示不加条件
     */
    public static String build(UserDataScope scope, DataPermission annotation, Table table) {
        if (scope == null || scope.getDataScope() == null || annotation == null || table == null) {
            return null;
        }

        // 一条 SQL 里 join 了多张表时，这个回调每张表都会进来一次，只在注解指定的别名上加条件
        String alias = table.getAlias() == null ? "" : table.getAlias().getName();
        if (!annotation.deptAlias().equals(alias)) {
            return null;
        }

        DataScopeEnum dataScope = IBaseEnum.getEnumByValue(scope.getDataScope(), DataScopeEnum.class);
        if (dataScope == null) {
            // 取值不在枚举里属于数据问题，按最小权限兜底，避免把不该看的数据放大出去
            log.warn("未知的数据范围 {}，按 SELF 处理", scope.getDataScope());
            dataScope = DataScopeEnum.SELF;
        }
        if (dataScope == DataScopeEnum.ALL) {
            return null;
        }

        String deptColumn = column(alias, annotation.deptField());
        String userAlias = StringUtils.hasText(annotation.userAlias()) ? annotation.userAlias() : alias;
        String userColumn = column(userAlias, annotation.userField());

        return switch (dataScope) {
            case DEPT -> scope.getDeptId() == null ? null : deptColumn + " = " + scope.getDeptId();
            case DEPT_AND_CHILD -> buildDeptIn(scope, deptColumn);
            case SELF -> scope.getUserId() == null ? null : userColumn + " = " + scope.getUserId();
            default -> null;
        };
    }

    private static String buildDeptIn(UserDataScope scope, String deptColumn) {
        Set<Long> deptIds = scope.getDeptIds();
        if (deptIds == null || deptIds.isEmpty()) {
            // 业务方没给部门集合时退化成只看本部门，范围只会变小，不会越权
            deptIds = scope.getDeptId() == null ? null : Set.of(scope.getDeptId());
        }
        if (deptIds == null || deptIds.isEmpty()) {
            return null;
        }
        String ids = deptIds.stream().map(String::valueOf).collect(Collectors.joining(","));
        return deptColumn + " IN (" + ids + ")";
    }

    private static String column(String alias, String field) {
        return StringUtils.hasText(alias) ? alias + "." + field : field;
    }
}
