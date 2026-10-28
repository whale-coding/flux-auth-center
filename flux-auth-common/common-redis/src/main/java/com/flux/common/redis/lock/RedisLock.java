package com.flux.common.redis.lock;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @ClassName RedisLock
 * @Author njq
 * @Date 2026/10/28
 * @Description 基于 Redisson 的分布式锁。锁是线程级别的，加锁和解锁必须在同一个线程里做，
 * 所以不要在加锁后把业务丢给线程池执行。
 */
@Component
public class RedisLock {

    private static final String LOCK_PREFIX = "lock:";

    /**
     * 不指定租期时 Redisson 的看门狗默认 30 秒续期
     */
    private static final Duration DEFAULT_LEASE_TIME = Duration.ofSeconds(30);

    private final RedissonClient redissonClient;

    public RedisLock(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    /**
     * 尝试加锁，最多等 waitTime 拿不到就返回 false。
     * leaseTime 传 null 或 0 表示交给看门狗续期（业务执行时间不确定时用这个）
     */
    public boolean tryLock(String key, Duration waitTime, Duration leaseTime) {
        RLock lock = redissonClient.getLock(lockKey(key));
        try {
            if (leaseTime == null || leaseTime.isZero() || leaseTime.isNegative()) {
                return lock.tryLock(waitTime.toMillis(), TimeUnit.MILLISECONDS);
            }
            return lock.tryLock(waitTime.toMillis(), leaseTime.toMillis(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * 不等待直接尝试加锁，持有 30 秒
     */
    public boolean tryLock(String key) {
        return tryLock(key, Duration.ZERO, DEFAULT_LEASE_TIME);
    }

    /**
     * 解锁。只由持有锁的线程解锁，没持有就直接跳过，否则 Redisson 会抛 IllegalMonitorStateException
     */
    public void unlock(String key) {
        RLock lock = redissonClient.getLock(lockKey(key));
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

    /**
     * 加锁执行业务，拿不到锁抛 IllegalStateException。业务抛异常时锁也会释放
     */
    public <T> T executeWithLock(String key, Duration waitTime, Duration leaseTime, Supplier<T> action) {
        if (!tryLock(key, waitTime, leaseTime)) {
            throw new IllegalStateException("获取分布式锁失败: " + key);
        }
        try {
            return action.get();
        } finally {
            unlock(key);
        }
    }

    public void executeWithLock(String key, Duration waitTime, Duration leaseTime, Runnable action) {
        executeWithLock(key, waitTime, leaseTime, () -> {
            action.run();
            return null;
        });
    }

    private String lockKey(String key) {
        return LOCK_PREFIX + key;
    }
}
