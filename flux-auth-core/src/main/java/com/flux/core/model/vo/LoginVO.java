package com.flux.core.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "登录成功返回的 token 信息")
public class LoginVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 请求头名称，默认 Authorization
     */
    @Schema(description = "请求头名称", example = "Authorization")
    private String tokenName;

    /**
     * token 值
     */
    @Schema(description = "token 值，后续请求放在 Authorization 请求头里", example = "ae1d3aa9-cda8-4e70-8a14-f70c1522c4c9")
    private String tokenValue;

    /**
     * 有效期，单位秒；-1 表示永不过期
     */
    @Schema(description = "有效期，单位秒；-1 表示永不过期", example = "2592000")
    private Long expiresIn;
}
