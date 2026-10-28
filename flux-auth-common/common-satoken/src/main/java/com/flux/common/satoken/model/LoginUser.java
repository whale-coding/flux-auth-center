package com.flux.common.satoken.model;

import cn.dev33.satoken.json.SaJsonType;
import lombok.Data;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @ClassName LoginUser
 * @Author njq
 * @Date 2026/10/28 22:30
 * @Description 登录用户信息，登录成功后写入 sa-token 的 token-session
 */
@Data
public class LoginUser implements Serializable, SaJsonType {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID（也是 sa-token 的 loginId）
     */
    private Long userId;

    /**
     * 登录账号
     */
    private String username;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 数据权限范围，取值见 common-mybatis-plus 的 DataScopeEnum
     */
    private Integer dataScope;

    /**
     * 角色编码集合
     */
    private Set<String> roles = new HashSet<>();

    /**
     * 权限标识集合
     */
    private Set<String> permissions = new HashSet<>();

    /**
     * 登录时间（毫秒时间戳）
     */
    private Long loginTime;
}
