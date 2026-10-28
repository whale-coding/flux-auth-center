package com.flux.common.satoken.helper;

import cn.dev33.satoken.stp.StpUtil;
import com.flux.common.core.result.ResultCode;
import com.flux.common.satoken.model.LoginUser;
import com.flux.common.web.exception.BizException;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Set;

/**
 * @ClassName LoginHelper
 * @Author njq
 * @Date 2026/10/28 22:32
 * @Description 登录状态读取工具类，业务代码统一通过它取当前登录人，不要直接调 StpUtil。
 * 只负责读；登录/登出请用 {@link com.flux.common.satoken.service.LoginService}
 */
@Slf4j
public final class LoginHelper {

    /**
     * LoginUser 在 token-session 中的 key
     */
    public static final String LOGIN_USER_KEY = "loginUser";

    private LoginHelper() {
    }

    /**
     * 是否已登录
     */
    public static boolean isLogin() {
        return StpUtil.isLogin();
    }

    /**
     * 获取当前登录用户，未登录或会话中无数据时返回 null
     */
    public static LoginUser getLoginUser() {
        if (!StpUtil.isLogin()) {
            return null;
        }
        try {
            Object value = StpUtil.getTokenSession().get(LOGIN_USER_KEY);
            return value instanceof LoginUser loginUser ? loginUser : null;
        } catch (Exception e) {
            // 子线程、定时任务等无 web 上下文的环境取不到 token，统一按未登录处理
            log.debug("获取登录用户失败", e);
            return null;
        }
    }

    /**
     * 获取当前登录用户，取不到直接抛业务异常
     */
    public static LoginUser getLoginUserOrThrow() {
        LoginUser loginUser = getLoginUser();
        if (loginUser == null) {
            throw new BizException(ResultCode.TOKEN_INVALID);
        }
        return loginUser;
    }

    /**
     * 当前用户ID，未登录返回 null
     */
    public static Long getUserId() {
        LoginUser loginUser = getLoginUser();
        return loginUser == null ? null : loginUser.getUserId();
    }

    /**
     * 当前用户ID，未登录抛异常
     */
    public static Long getUserIdOrThrow() {
        return getLoginUserOrThrow().getUserId();
    }

    /**
     * 当前用户名，未登录返回 null
     */
    public static String getUsername() {
        LoginUser loginUser = getLoginUser();
        return loginUser == null ? null : loginUser.getUsername();
    }

    /**
     * 当前用户所属部门ID，未登录返回 null
     */
    public static Long getDeptId() {
        LoginUser loginUser = getLoginUser();
        return loginUser == null ? null : loginUser.getDeptId();
    }

    /**
     * 当前用户角色编码集合，未登录返回空集合
     */
    public static Set<String> getRoles() {
        LoginUser loginUser = getLoginUser();
        return loginUser == null || loginUser.getRoles() == null ? Collections.emptySet() : loginUser.getRoles();
    }

    /**
     * 当前用户权限标识集合，未登录返回空集合
     */
    public static Set<String> getPermissions() {
        LoginUser loginUser = getLoginUser();
        return loginUser == null || loginUser.getPermissions() == null ? Collections.emptySet() : loginUser.getPermissions();
    }

    /**
     * 当前请求携带的 token 值
     */
    public static String getTokenValue() {
        return StpUtil.getTokenValue();
    }

    /**
     * 是否拥有指定权限
     */
    public static boolean hasPermission(String permission) {
        return StpUtil.hasPermission(permission);
    }

    /**
     * 是否拥有指定角色
     */
    public static boolean hasRole(String role) {
        return StpUtil.hasRole(role);
    }
}
