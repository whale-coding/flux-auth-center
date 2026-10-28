package com.flux.common.satoken.service;

import cn.dev33.satoken.SaManager;
import com.flux.common.core.constant.RedisConstants;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * @ClassName RolePermCache
 * @Author njq
 * @Date 2026/10/28 22:55
 * @Description 角色/权限的 Redis 缓存。
 * 用 Redis Set 存，key 形如 role_perms:{userId}:roles，过期时间和 sa-token 的 token 有效期保持一致。
 * 没有缓存在本地内存，是为了多实例部署时改完权限能立刻生效 —— 需要刷新就调 refresh/evict。
 */
@Component
public class RolePermCache {

    private static final String ROLES_SUFFIX = ":roles";
    private static final String PERMS_SUFFIX = ":perms";

    private final StringRedisTemplate redisTemplate;

    public RolePermCache(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 写入角色和权限，登录成功时调用
     */
    public void save(Long userId, Collection<String> roles, Collection<String> permissions) {
        if (userId == null) {
            return;
        }
        write(userId, ROLES_SUFFIX, roles);
        write(userId, PERMS_SUFFIX, permissions);
    }

    /**
     * 删除后重新写入，后台改了角色或权限后调这个，用户无需重新登录
     */
    public void refresh(Long userId, Collection<String> roles, Collection<String> permissions) {
        evict(userId);
        save(userId, roles, permissions);
    }

    public Set<String> getRoles(Long userId) {
        return read(userId, ROLES_SUFFIX);
    }

    public Set<String> getPermissions(Long userId) {
        return read(userId, PERMS_SUFFIX);
    }

    /**
     * 缓存是否存在。注意不能用「权限集合为空」判断，因为权限被全部收回时集合也是空的
     */
    public boolean exists(Long userId) {
        if (userId == null) {
            return false;
        }
        return Boolean.TRUE.equals(redisTemplate.hasKey(key(userId, ROLES_SUFFIX)))
                || Boolean.TRUE.equals(redisTemplate.hasKey(key(userId, PERMS_SUFFIX)));
    }

    public void evict(Long userId) {
        if (userId == null) {
            return;
        }
        redisTemplate.delete(key(userId, ROLES_SUFFIX));
        redisTemplate.delete(key(userId, PERMS_SUFFIX));
    }

    private void write(Long userId, String suffix, Collection<String> values) {
        String key = key(userId, suffix);
        redisTemplate.delete(key);
        if (values == null || values.isEmpty()) {
            return;
        }
        redisTemplate.opsForSet().add(key, values.toArray(new String[0]));

        // 和 token 有效期对齐，避免 token 还有效但权限已过期
        long timeout = SaManager.getConfig().getTimeout();
        if (timeout > 0) {
            redisTemplate.expire(key, Duration.ofSeconds(timeout));
        }
    }

    private Set<String> read(Long userId, String suffix) {
        if (userId == null) {
            return Collections.emptySet();
        }
        Set<String> members = redisTemplate.opsForSet().members(key(userId, suffix));
        return members == null ? Collections.emptySet() : members;
    }

    private String key(Long userId, String suffix) {
        return RedisConstants.ROLE_PERMS_PREFIX + userId + suffix;
    }
}
