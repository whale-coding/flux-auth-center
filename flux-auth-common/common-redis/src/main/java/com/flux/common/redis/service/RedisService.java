package com.flux.common.redis.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collection;
import java.util.function.Supplier;

/**
 * @ClassName RedisService
 * @Author njq
 * @Date 2026/10/28
 * @Description Redis 常用操作封装。只覆盖缓存场景（value、过期、计数、防击穿），
 * 需要 hash、list、set、zset 这类结构直接注入 RedisTemplate 用。
 */
@Component
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // ==================== key ====================

    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public boolean delete(String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }

    /**
     * 批量删除，返回实际删除的数量
     */
    public long delete(Collection<String> keys) {
        if (keys == null || keys.isEmpty()) {
            return 0L;
        }
        Long count = redisTemplate.delete(keys);
        return count == null ? 0L : count;
    }

    /**
     * 设置过期时间，返回 false 表示 key 不存在
     */
    public boolean expire(String key, Duration timeout) {
        return Boolean.TRUE.equals(redisTemplate.expire(key, timeout));
    }

    /**
     * 剩余过期时间，key 不存在或没有设置过期时间时返回 null
     */
    public Duration getExpire(String key) {
        Long seconds = redisTemplate.getExpire(key);
        return seconds == null || seconds <= 0 ? null : Duration.ofSeconds(seconds);
    }

    // ==================== value ====================

    /**
     * 写入并覆盖原值，不过期
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 写入并设置过期时间
     */
    public void set(String key, Object value, Duration timeout) {
        redisTemplate.opsForValue().set(key, value, timeout);
    }

    /**
     * 读取，不存在返回 null
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 读取并按指定类型返回。类型对不上（比如缓存里是旧版本的对象结构）时返回 null，不会抛类型转换异常
     */
    public <T> T get(String key, Class<T> type) {
        Object value = redisTemplate.opsForValue().get(key);
        return type.isInstance(value) ? type.cast(value) : null;
    }

    /**
     * 仅当 key 不存在时写入（分布式占位、防重复提交）
     */
    public boolean setIfAbsent(String key, Object value, Duration timeout) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, value, timeout));
    }

    /**
     * 自增，返回自增后的值。key 不存在时按 0 起算
     */
    public long increment(String key, long delta) {
        Long value = redisTemplate.opsForValue().increment(key, delta);
        return value == null ? 0L : value;
    }

    /**
     * 读缓存，没有再加载并回填。注意不缓存 null，避免缓存穿透要靠调用方自己处理
     */
    public <T> T getOrLoad(String key, Class<T> type, Duration timeout, Supplier<T> loader) {
        T cached = get(key, type);
        if (cached != null) {
            return cached;
        }
        T value = loader.get();
        if (value != null) {
            set(key, value, timeout);
        }
        return value;
    }
}
