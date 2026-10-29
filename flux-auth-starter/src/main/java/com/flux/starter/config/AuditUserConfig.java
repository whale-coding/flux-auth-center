package com.flux.starter.config;

import com.flux.common.mybatis.handler.AuditUserProvider;
import com.flux.common.satoken.helper.LoginHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName AuditUserConfig
 * @Author njq
 * @Date 2026/10/29
 * @Description 审计字段数据源：从登录态取当前用户ID，供 create_by / update_by 自动填充。
 * 定时任务、匿名执行等没有登录态的场景返回 null，审计字段留空。
 */
@Configuration
public class AuditUserConfig {

    @Bean
    public AuditUserProvider auditUserProvider() {
        return LoginHelper::getUserId;
    }
}
