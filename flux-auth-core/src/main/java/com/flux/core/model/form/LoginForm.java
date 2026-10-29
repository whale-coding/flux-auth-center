package com.flux.core.model.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @ClassName LoginForm
 * @Author njq
 * @Date 2026/10/29
 * @Description 登录入参
 */
@Data
public class LoginForm {

    @NotBlank(message = "用户名不能为空")
    @Size(max = 64, message = "用户名长度不能超过 64 个字符")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(max = 128, message = "密码长度不能超过 128 个字符")
    private String password;
}
