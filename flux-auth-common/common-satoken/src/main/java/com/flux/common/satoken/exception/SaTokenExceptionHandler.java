package com.flux.common.satoken.exception;

import cn.dev33.satoken.exception.DisableServiceException;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.exception.SaTokenException;
import com.flux.common.core.result.Result;
import com.flux.common.core.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @ClassName SaTokenExceptionHandler
 * @Author njq
 * @Date 2026/10/28 22:40
 * @Description sa-token 相关异常处理。
 * 单独放在 common-satoken，避免 common-web 反向依赖 sa-token；
 * 优先级调到最高，保证这些异常不会落到 common-web 的 Exception 兜底里变成“系统执行出错”
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class SaTokenExceptionHandler {

    /**
     * 未登录 / token 无效
     */
    @ExceptionHandler(NotLoginException.class)
    public Result<?> handleNotLoginException(NotLoginException e) {
        String msg = resolveNotLoginMessage(e.getType());
        log.warn("未登录访问: type={}, msg={}", e.getType(), msg);
        return Result.failed(ResultCode.TOKEN_INVALID, msg);
    }

    /**
     * 缺少指定权限
     */
    @ExceptionHandler(NotPermissionException.class)
    public Result<?> handleNotPermissionException(NotPermissionException e) {
        log.warn("缺少权限: {}", e.getPermission());
        return Result.failed(ResultCode.ACCESS_UNAUTHORIZED, "缺少权限: " + e.getPermission());
    }

    /**
     * 缺少指定角色
     */
    @ExceptionHandler(NotRoleException.class)
    public Result<?> handleNotRoleException(NotRoleException e) {
        log.warn("缺少角色: {}", e.getRole());
        return Result.failed(ResultCode.ACCESS_UNAUTHORIZED, "缺少角色: " + e.getRole());
    }

    /**
     * 账号被封禁 / 服务被禁用
     */
    @ExceptionHandler(DisableServiceException.class)
    public Result<?> handleDisableServiceException(DisableServiceException e) {
        log.warn("账号被封禁: service={}, level={}", e.getService(), e.getLevel());
        return Result.failed(ResultCode.USER_ACCOUNT_LOCKED);
    }

    /**
     * 其他 sa-token 异常兜底
     */
    @ExceptionHandler(SaTokenException.class)
    public Result<?> handleSaTokenException(SaTokenException e) {
        log.warn("认证异常: {}", e.getMessage());
        return Result.failed(ResultCode.AUTHORIZED_ERROR, e.getMessage());
    }

    private String resolveNotLoginMessage(String type) {
        if (NotLoginException.NOT_TOKEN.equals(type)) {
            return "未提供认证令牌，请先登录";
        }
        if (NotLoginException.INVALID_TOKEN.equals(type)) {
            return "认证令牌无效，请重新登录";
        }
        if (NotLoginException.TOKEN_TIMEOUT.equals(type)) {
            return "登录已过期，请重新登录";
        }
        if (NotLoginException.BE_REPLACED.equals(type)) {
            return "账号已在其他设备登录";
        }
        if (NotLoginException.KICK_OUT.equals(type)) {
            return "账号已被强制下线";
        }
        if (NotLoginException.TOKEN_FREEZE.equals(type)) {
            return "账号已被封禁";
        }
        return ResultCode.USER_LOGIN_ERROR.getMsg();
    }
}
