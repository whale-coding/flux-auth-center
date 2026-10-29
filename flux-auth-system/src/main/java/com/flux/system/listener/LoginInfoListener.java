package com.flux.system.listener;

import com.flux.common.log.event.LoginInfoEvent;
import com.flux.system.service.LoginLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @ClassName LoginInfoListener
 * @Author njq
 * @Date 2026/10/29
 * @Description 登录日志事件监听：只做事件到 service 的转发，落库逻辑在 LoginLogService
 */
@Component
@RequiredArgsConstructor
public class LoginInfoListener {

    private final LoginLogService loginLogService;

    @EventListener
    public void onLoginInfo(LoginInfoEvent event) {
        loginLogService.record(event);
    }
}
