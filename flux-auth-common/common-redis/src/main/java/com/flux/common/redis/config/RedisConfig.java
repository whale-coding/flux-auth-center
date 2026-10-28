package com.flux.common.redis.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;

/**
 * @ClassName RedisConfig
 * @Author njq
 * @Date 2026/10/28
 * @Description RedisTemplate 配置：key 用字符串、value 用 JSON。
 * value 序列化会带上 @class 类型信息，所以取出来不用再指定类型也能还原成原对象；
 * 反序列化时用白名单限制可还原的类型，避免被构造恶意 JSON 触发 gadget 链。
 * <p>
 * 排在 DataRedisAutoConfiguration 前面，才能覆盖它默认注册的 redisTemplate（默认是 JDK 序列化）
 */
@AutoConfiguration(before = DataRedisAutoConfiguration.class)
public class RedisConfig {

    @Bean
    @ConditionalOnMissingBean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        GenericJacksonJsonRedisSerializer jsonSerializer = jsonSerializer();

        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);
        template.afterPropertiesSet();
        return template;
    }

    /**
     * 只允许还原业务类、集合、JDK 基础类型，其它类一律拒绝。
     * 业务包不是 com.flux 的话，这里要跟着改
     */
    private GenericJacksonJsonRedisSerializer jsonSerializer() {
        PolymorphicTypeValidator typeValidator = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType("com.flux.")
                .allowIfSubType("java.util.")
                .allowIfSubType("java.lang.")
                .allowIfSubType("java.time.")
                .allowIfSubType("java.math.")
                .build();
        return GenericJacksonJsonRedisSerializer.builder()
                .enableDefaultTyping(typeValidator)
                .build();
    }
}
