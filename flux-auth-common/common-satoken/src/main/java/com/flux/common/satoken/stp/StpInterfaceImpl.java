package com.flux.common.satoken.stp;

import cn.dev33.satoken.stp.StpInterface;
import com.flux.common.satoken.helper.LoginHelper;
import com.flux.common.satoken.model.LoginUser;
import com.flux.common.satoken.service.RolePermCache;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @ClassName StpInterfaceImpl
 * @Author njq
 * @Date 2026/10/28 22:35
 * @Description sa-token 权限数据源：优先读 Redis 缓存，缓存缺失时退回 token-session
 */
@Component
public class StpInterfaceImpl implements StpInterface {

    private final RolePermCache rolePermCache;

    public StpInterfaceImpl(RolePermCache rolePermCache) {
        this.rolePermCache = rolePermCache;
    }

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        Long userId = parseUserId(loginId);
        if (rolePermCache.exists(userId)) {
            return new ArrayList<>(rolePermCache.getPermissions(userId));
        }
        // 缓存整体缺失（比如被手动 evict）才退回 token-session；缓存存在但为空，说明权限被收回，直接拒绝
        LoginUser loginUser = LoginHelper.getLoginUser();
        if (loginUser == null || loginUser.getPermissions() == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(loginUser.getPermissions());
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        Long userId = parseUserId(loginId);
        if (rolePermCache.exists(userId)) {
            return new ArrayList<>(rolePermCache.getRoles(userId));
        }
        LoginUser loginUser = LoginHelper.getLoginUser();
        if (loginUser == null || loginUser.getRoles() == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(loginUser.getRoles());
    }

    /**
     * loginId 是我们登录时传的 userId
     */
    private Long parseUserId(Object loginId) {
        if (loginId instanceof Number number) {
            return number.longValue();
        }
        if (loginId == null) {
            return null;
        }
        try {
            return Long.valueOf(loginId.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
