package com.flux.common.openapi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @ClassName OpenApiConfig
 * @Author njq
 * @Date 2026/10/28 11:05
 * @Description 接口文档自动配置：注册 OpenAPI 基本信息和鉴权头
 */
@AutoConfiguration
@ConditionalOnClass(OpenAPI.class)
@EnableConfigurationProperties(DocProperties.class)
@ConditionalOnProperty(prefix = "doc", name = "enable", havingValue = "true", matchIfMissing = true)
public class OpenApiConfig {
    @Bean
    @ConditionalOnMissingBean
    public OpenAPI openAPI(DocProperties properties) {
        return new OpenAPI()
                .info(new Info()
                        .title(properties.getTitle())
                        .description(properties.getDescription())
                        .version(properties.getVersion())
                )
                // 鉴权头：填登录接口返回的 tokenValue，直接放请求头，不加 Bearer 前缀
                .components(new Components().addSecuritySchemes(properties.getAuthHeader(),
                        new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name(properties.getAuthHeader())
                                .description("登录接口返回的 tokenValue")
                ));
    }
}
