package com.flux.common.mybatis.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @ClassName MyMetaObjectHandler
 * @Author njq
 * @Date 2026/10/28 15:09
 * @Description mybatis-plus 字段自动填充。
 * 实体不一定有全部字段（比如日志表没有 create_by），所以用 fillStrategy/setIfPresent，
 * 字段不存在时直接跳过，而不是像 strictFill 那样要求实体字段上标 @TableField(fill)。
 * 参考文档：https://baomidou.com/guides/auto-fill-field/
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    private final ObjectProvider<AuditUserProvider> auditUserProvider;

    public MyMetaObjectHandler(ObjectProvider<AuditUserProvider> auditUserProvider) {
        this.auditUserProvider = auditUserProvider;
    }

    /**
     * 新增：填充创建人、更新人、创建时间、更新时间
     * @param metaObject 元对象
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        fillIfNull(metaObject, "createTime", now);
        fillIfNull(metaObject, "updateTime", now);

        Long userId = currentUserId();
        if (userId != null) {
            fillIfNull(metaObject, "createBy", userId);
            fillIfNull(metaObject, "updateBy", userId);
        }
    }

    /**
     * 更新：强制覆盖更新人和更新时间，避免实体里带出来的旧值写回库
     * @param metaObject 元对象
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        setIfPresent(metaObject, "updateTime", LocalDateTime.now());
        Long userId = currentUserId();
        if (userId != null) {
            setIfPresent(metaObject, "updateBy", userId);
        }
    }

    private Long currentUserId() {
        AuditUserProvider provider = auditUserProvider.getIfAvailable();
        return provider == null ? null : provider.getCurrentUserId();
    }

    /**
     * 只在字段当前为空时填充，保留调用方显式设置的值
     */
    private void fillIfNull(MetaObject metaObject, String fieldName, Object value) {
        if (value == null || !metaObject.hasSetter(fieldName)) {
            return;
        }
        if (metaObject.hasGetter(fieldName) && metaObject.getValue(fieldName) != null) {
            return;
        }
        metaObject.setValue(fieldName, value);
    }

    private void setIfPresent(MetaObject metaObject, String fieldName, Object value) {
        if (value != null && metaObject.hasSetter(fieldName)) {
            metaObject.setValue(fieldName, value);
        }
    }
}
