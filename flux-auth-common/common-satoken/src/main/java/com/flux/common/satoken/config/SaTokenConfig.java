package com.flux.common.satoken.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @ClassName SaTokenConfig
 * @Author njq
 * @Date 2026/10/28 22:26
 * @Description sa-token 拦截器配置：默认所有接口都要登录，@SaCheckPermission 等注解鉴权同时生效
 */
@AutoConfiguration
@EnableConfigurationProperties(AuthProperties.class)
public class SaTokenConfig implements WebMvcConfigurer {

    private final AuthProperties authProperties;

    public SaTokenConfig(AuthProperties authProperties) {
        this.authProperties = authProperties;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handler -> {
            // 全局登录校验可关闭；即使关闭，方法上的 @SaCheckLogin/@SaCheckPermission 依然会被检查
            if (!authProperties.isEnabled()) {
                return;
            }
            SaRouter.match("/**")
                    .notMatch(authProperties.getExcludes())
                    .check(r -> StpUtil.checkLogin());
        })).addPathPatterns("/**");
    }
}
