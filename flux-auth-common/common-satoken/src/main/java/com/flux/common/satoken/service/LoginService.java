package com.flux.common.satoken.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import com.flux.common.satoken.helper.LoginHelper;
import com.flux.common.satoken.model.LoginUser;
import org.springframework.stereotype.Component;

/**
 * @ClassName LoginService
 * @Author njq
 * @Date 2026/10/28 22:58
 * @Description 登录/登出入口。写操作走 Spring Bean，读操作走 {@link LoginHelper} 的静态方法
 */
@Component
public class LoginService {

    private final RolePermCache rolePermCache;

    public LoginService(RolePermCache rolePermCache) {
        this.rolePermCache = rolePermCache;
    }

    /**
     * 登录：签发 token、把用户信息写进 token-session、把角色权限写进 Redis
     */
    public void login(LoginUser loginUser) {
        StpUtil.login(loginUser.getUserId());
        afterLogin(loginUser);
    }

    /**
     * 登录，支持自定义参数（有效期、设备类型等）
     */
    public void login(LoginUser loginUser, SaLoginParameter parameter) {
        StpUtil.login(loginUser.getUserId(), parameter);
        afterLogin(loginUser);
    }

    /**
     * 注销当前会话。角色权限缓存不清，避免同一账号的其他端登录态受影响，
     * 需要清理时用 {@link RolePermCache#evict(Long)}
     */
    public void logout() {
        StpUtil.logout();
    }

    private void afterLogin(LoginUser loginUser) {
        StpUtil.getTokenSession().set(LoginHelper.LOGIN_USER_KEY, loginUser);
        rolePermCache.save(loginUser.getUserId(), loginUser.getRoles(), loginUser.getPermissions());
    }
}
