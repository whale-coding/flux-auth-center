package com.flux.common.core.constant;

/**
 * @ClassName RedisConstants
 * @Author njq
 * @Date 2026/10/28 14:57
 * @Description Redis常量
 */
public interface RedisConstants {
    /**
     * 黑名单TOKEN Key前缀
     */
    String TOKEN_BLACKLIST_PREFIX = "token:blacklist:";

    /**
     * 角色和权限缓存前缀
     */
    String ROLE_PERMS_PREFIX = "role_perms:";
}
