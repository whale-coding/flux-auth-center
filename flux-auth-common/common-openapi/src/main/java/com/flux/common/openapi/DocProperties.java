package com.flux.common.openapi;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @ClassName DocProperties
 * @Author njq
 * @Date 2026/10/28 09:44
 * @Description 接口文档自定义配置
 */
@Data
@ConfigurationProperties(prefix = "doc")
public class DocProperties {
    /**
     * 是否开启文档，默认true
     */
    private Boolean enable = true;

    /**
     * 标题
     */
    private String title = "API接口文档";

    /**
     * 描述
     */
    private String description = "接口文档";

    /**
     * 版本
     */
    private String version = "1.0.0";

    /**
     * 鉴权请求头名称，要和 sa-token 的 token-name 保持一致
     */
    private String authHeader = "Authorization";
}
