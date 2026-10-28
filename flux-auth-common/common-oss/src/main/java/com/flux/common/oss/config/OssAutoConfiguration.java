package com.flux.common.oss.config;

import com.flux.common.oss.template.OssTemplate;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

/**
 * @ClassName OssAutoConfiguration
 * @Author njq
 * @Date 2026/10/28
 * @Description 对象存储自动装配。只有配了 oss.endpoint 才会生效，
 * 没配的工程引入这个模块也不会因为缺少配置启动失败
 */
@AutoConfiguration
@EnableConfigurationProperties(OssProperties.class)
@ConditionalOnProperty(prefix = "oss", name = "endpoint")
public class OssAutoConfiguration {

    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean
    public S3Client s3Client(OssProperties properties) {
        return S3Client.builder()
                .endpointOverride(URI.create(properties.getEndpoint()))
                .region(region(properties))
                .credentialsProvider(credentialsProvider(properties))
                .forcePathStyle(properties.isPathStyleAccess())
                .build();
    }

    /**
     * 生成临时下载地址用，和 S3Client 分开是因为 presigner 需要自己的 serviceConfiguration
     */
    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean
    public S3Presigner s3Presigner(OssProperties properties) {
        return S3Presigner.builder()
                .endpointOverride(URI.create(properties.getEndpoint()))
                .region(region(properties))
                .credentialsProvider(credentialsProvider(properties))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(properties.isPathStyleAccess())
                        .build())
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    public OssTemplate ossTemplate(S3Client s3Client, S3Presigner s3Presigner, OssProperties properties) {
        return new OssTemplate(s3Client, s3Presigner, properties);
    }

    private Region region(OssProperties properties) {
        return Region.of(StringUtils.hasText(properties.getRegion()) ? properties.getRegion() : "us-east-1");
    }

    private AwsCredentialsProvider credentialsProvider(OssProperties properties) {
        return StaticCredentialsProvider.create(
                AwsBasicCredentials.create(properties.getAccessKey(), properties.getSecretKey()));
    }
}
