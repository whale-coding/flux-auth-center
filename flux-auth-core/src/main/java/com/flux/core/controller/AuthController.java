package com.flux.core.controller;

import com.flux.common.core.result.Result;
import com.flux.common.satoken.helper.LoginHelper;
import com.flux.common.satoken.model.LoginUser;
import com.flux.core.model.form.LoginForm;
import com.flux.core.model.vo.LoginVO;
import com.flux.core.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName AuthController
 * @Author njq
 * @Date 2026/10/29
 * @Description 认证接口：登录、登出、当前登录人
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 登录，返回 token
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody @Valid LoginForm form) {
        return Result.success(authService.login(form));
    }

    /**
     * 注销当前会话
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout();
        return Result.success();
    }

    /**
     * 当前登录人信息，前端刷新页面时用它恢复角色和权限
     */
    @GetMapping("/me")
    public Result<LoginUser> me() {
        return Result.success(LoginHelper.getLoginUserOrThrow());
    }
}
