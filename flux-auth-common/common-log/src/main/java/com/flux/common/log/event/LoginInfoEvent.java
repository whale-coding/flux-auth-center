package com.flux.common.log.event;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName LoginInfoEvent
 * @Author njq
 * @Date 2026/10/28 20:30
 * @Description 登录事件
 */
@Data
public class LoginInfoEvent implements Serializable {
    /**
     * 用户账号
     */
    private String username;

    /**
     * 登录状态 0成功 1失败
     */
    private String status;

    /**
     * 提示消息
     */
    private String message;

    /**
     * 请求体
     */
    private HttpServletRequest request;

    /**
     * 其他参数
     */
    private Object[] args;
}
