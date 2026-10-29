package com.flux.system.listener;

import com.flux.common.log.event.LoginInfoEvent;
import com.flux.system.mapper.SysLogininforMapper;
import com.flux.system.model.entity.SysLogininfor;
import com.flux.system.util.ClientInfoUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @ClassName LoginInfoListener
 * @Author njq
 * @Date 2026/10/29
 * @Description 登录日志落库：监听 common-log 发出的 LoginInfoEvent，写 sys_logininfor。
 * 落库失败只记错误日志，不能反过来影响登录流程
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LoginInfoListener {

    /**
     * sys_logininfor.status：0 成功，1 失败
     */
    private static final String EVENT_STATUS_SUCCESS = "0";

    private static final int MAX_USERNAME_LENGTH = 50;
    private static final int MAX_MSG_LENGTH = 255;

    private final SysLogininforMapper logininforMapper;

    @EventListener
    public void onLoginInfo(LoginInfoEvent event) {
        try {
            SysLogininfor record = new SysLogininfor();
            record.setUserName(ClientInfoUtils.truncate(event.getUsername(), MAX_USERNAME_LENGTH));
            record.setStatus(EVENT_STATUS_SUCCESS.equals(event.getStatus()) ? 0 : 1);
            record.setMsg(ClientInfoUtils.truncate(event.getMessage(), MAX_MSG_LENGTH));
            record.setLoginTime(LocalDateTime.now());

            HttpServletRequest request = event.getRequest();
            if (request != null) {
                String userAgent = request.getHeader("User-Agent");
                record.setIpaddr(ClientInfoUtils.getClientIp(request));
                record.setBrowser(ClientInfoUtils.getBrowser(userAgent));
                record.setOs(ClientInfoUtils.getOs(userAgent));
            }
            logininforMapper.insert(record);
        } catch (Exception e) {
            log.error("登录日志落库失败, username={}", event.getUsername(), e);
        }
    }
}
