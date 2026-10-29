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
     * 用户头像
     */
    private String avatar;

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
     * 数据权限可访问的部门集合：
     * DEPT_AND_CHILD 时是当前部门及子部门，CUSTOM 时是角色配置的 sys_role_dept，
     * 其它范围为空。登录时算好放进会话，避免每次请求查库
     */
    private Set<Long> dataScopeDeptIds = new HashSet<>();

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
