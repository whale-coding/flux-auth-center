package com.flux.common.oss.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * @ClassName OssProperties
 * @Author njq
 * @Date 2026/10/28
 * @Description 对象存储配置。RustFS 走 S3 协议，所以 endpoint 填 RustFS 的地址即可
 */
@Data
@Validated
@ConfigurationProperties(prefix = "oss")
public class OssProperties {

    /**
     * 服务地址，如 http://localhost:9000
     */
    @NotBlank
    private String endpoint;

    @NotBlank
    private String accessKey;

    @NotBlank
    private String secretKey;

    @NotBlank
    private String bucket;

    /**
     * S3 协议要求必须有 region，RustFS 不校验，保持默认即可
     */
    private String region = "us-east-1";

    /**
     * 是否用 path-style 访问（http://endpoint/bucket/key）。
     * RustFS、MinIO 这类自建服务必须开启
     */
    private boolean pathStyleAccess = true;

    /**
     * 对外访问地址前缀，配了 CDN 或自定义域名时填这里。
     * 不配则用 endpoint + bucket 拼出来的直连地址
     */
    private String publicUrl;
}
