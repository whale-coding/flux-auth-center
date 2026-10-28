package com.flux.common.knife4j;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @ClassName Knife4jAutoConfig
 * @Author njq
 * @Date 2026/10/28 11:05
 * @Description Knife4j自动配置类
 */
@AutoConfiguration
@ConditionalOnClass(OpenAPI.class)
@EnableConfigurationProperties(DocProperties.class)
@ConditionalOnProperty(prefix = "doc", name = "enable", havingValue = "true", matchIfMissing = true)
public class Knife4jAutoConfig {
    @Bean
    @ConditionalOnMissingBean
    public OpenAPI openAPI(DocProperties properties) {
        return new OpenAPI()
                .info(new Info()
                        .title(properties.getTitle())
                        .description(properties.getDescription())
                        .version(properties.getVersion())
                );
    }
}
