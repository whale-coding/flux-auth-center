package com.flux.core.controller;

import com.flux.common.core.result.Result;
import com.flux.common.satoken.helper.LoginHelper;
import com.flux.common.satoken.model.LoginUser;
import com.flux.core.model.form.LoginForm;
import com.flux.core.model.vo.LoginVO;
import com.flux.core.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "认证管理", description = "登录、登出、当前登录人")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 登录，返回 token
     */
    @Operation(summary = "登录", description = "用户名 + 密码，成功后返回 token，后续请求把 tokenValue 放在 Authorization 请求头")
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody @Valid LoginForm form) {
        return Result.success(authService.login(form));
    }

    /**
     * 注销当前会话
     */
    @Operation(summary = "登出", description = "注销当前会话，token 立即失效")
    @SecurityRequirement(name = "Authorization")
    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout();
        return Result.success();
    }

    /**
     * 当前登录人信息，前端刷新页面时用它恢复角色和权限
     */
    @Operation(summary = "当前登录人", description = "返回登录时写入会话的 LoginUser，前端刷新页面时用它恢复角色和权限")
    @SecurityRequirement(name = "Authorization")
    @GetMapping("/me")
    public Result<LoginUser> me() {
        return Result.success(LoginHelper.getLoginUserOrThrow());
    }
}
