package com.flux.common.web.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @ClassName CorsProperties
 * @Author njq
 * @Date 2026/10/28 21:30
 * @Description 跨域配置
 */
@Data
@ConfigurationProperties(prefix = "cors")
public class CorsProperties {

    /**
     * 是否开启跨域
     */
    private boolean enabled = true;

    /**
     * 允许的源，支持通配符，例如 https://*.flux.com
     */
    private String[] allowedOriginPatterns = {"*"};

    /**
     * 允许的请求方法
     */
    private String[] allowedMethods = {"GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"};

    /**
     * 允许的请求头
     */
    private String[] allowedHeaders = {"*"};

    /**
     * 暴露给前端的响应头
     */
    private String[] exposedHeaders = {"*"};

    /**
     * 是否允许携带凭证（Cookie、Authorization）
     * 注意：开启后必须使用 allowedOriginPatterns，不能使用 allowedOrigins("*")
     */
    private boolean allowCredentials = true;

    /**
     * 预检请求（OPTIONS）缓存时间，单位秒
     */
    private long maxAge = 3600;
}
