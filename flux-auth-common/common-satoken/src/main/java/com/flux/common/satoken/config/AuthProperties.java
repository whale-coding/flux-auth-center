package com.flux.common.satoken.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName AuthProperties
 * @Author njq
 * @Date 2026/10/28 22:28
 * @Description 认证相关配置
 */
@Data
@ConfigurationProperties(prefix = "auth")
public class AuthProperties {

    /**
     * 是否开启全局登录校验，关闭后所有接口都可以匿名访问（注解鉴权仍然生效）
     */
    private boolean enabled = true;

    /**
     * 免登录放行的路径，支持 Ant 风格
     */
    private List<String> excludes = new ArrayList<>(List.of(
            "/auth/login",
            "/auth/register",
            "/auth/captcha",
            "/captcha/**",
            "/error",
            "/favicon.ico",
            "/doc.html",
            "/webjars/**",
            "/v3/api-docs/**",
            "/swagger-ui/**"
    ));
}
