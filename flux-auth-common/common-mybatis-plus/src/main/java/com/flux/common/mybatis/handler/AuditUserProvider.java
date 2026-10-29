package com.flux.common.mybatis.handler;

/**
 * @ClassName AuditUserProvider
 * @Author njq
 * @Date 2026/10/29
 * @Description 审计字段取数 SPI：由业务模块提供当前登录人ID，用于自动填充 create_by / update_by。
 * common-mybatis-plus 不依赖认证模块，没有实现时审计字段留空，不影响其它功能。
 */
@FunctionalInterface
public interface AuditUserProvider {

    /**
     * 当前登录人ID，未登录返回 null
     */
    Long getCurrentUserId();
}
