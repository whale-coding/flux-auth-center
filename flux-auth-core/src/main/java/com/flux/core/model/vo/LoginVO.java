package com.flux.core.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName LoginVO
 * @Author njq
 * @Date 2026/10/29
 * @Description 登录成功返回的 token 信息
 */
@Data
@AllArgsConstructor
public class LoginVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 请求头名称，默认 Authorization
     */
    private String tokenName;

    /**
     * token 值
     */
    private String tokenValue;

    /**
     * 有效期，单位秒；-1 表示永不过期
     */
    private Long expiresIn;
}
