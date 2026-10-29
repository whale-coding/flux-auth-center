package com.flux.core.service;

import com.flux.core.model.form.LoginForm;
import com.flux.core.model.vo.LoginVO;

/**
 * @ClassName AuthService
 * @Author njq
 * @Date 2026/10/29
 * @Description 认证链路：登录、登出。业务代码通过 Controller 调用，不要绕过这里直接用 StpUtil
 */
public interface AuthService {

    /**
     * 登录，失败抛 {@link com.flux.common.web.exception.BizException}
     */
    LoginVO login(LoginForm form);

    /**
     * 注销当前会话
     */
    void logout();
}
