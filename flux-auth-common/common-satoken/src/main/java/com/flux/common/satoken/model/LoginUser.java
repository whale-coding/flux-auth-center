package com.flux.common.satoken.model;

import cn.dev33.satoken.json.SaJsonType;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "登录用户信息")
public class LoginUser implements Serializable, SaJsonType {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID（也是 sa-token 的 loginId）
     */
    @Schema(description = "用户ID", example = "1")
    private Long userId;

    /**
     * 登录账号
     */
    @Schema(description = "登录账号", example = "admin")
    private String username;

    /**
     * 用户昵称
     */
    @Schema(description = "用户昵称", example = "超级管理员")
    private String nickname;

    /**
     * 用户头像
     */
    @Schema(description = "用户头像地址")
    private String avatar;

    /**
     * 部门ID
     */
    @Schema(description = "部门ID", example = "1")
    private Long deptId;

    /**
     * 部门名称
     */
    @Schema(description = "部门名称", example = "Flux 科技")
    private String deptName;

    /**
     * 数据权限范围，取值见 common-mybatis-plus 的 DataScopeEnum
     */
    @Schema(description = "数据权限范围：0 全部、1 自定义、2 本部门、3 本部门及以下、4 仅本人", example = "0")
    private Integer dataScope;

    /**
     * 数据权限可访问的部门集合：
     * DEPT_AND_CHILD 时是当前部门及子部门，CUSTOM 时是角色配置的 sys_role_dept，
     * 其它范围为空。登录时算好放进会话，避免每次请求查库
     */
    @Schema(description = "数据权限可访问的部门集合，仅自定义/本部门及以下时有值")
    private Set<Long> dataScopeDeptIds = new HashSet<>();

    /**
     * 角色编码集合
     */
    @Schema(description = "角色编码集合", example = "[\"ROOT\"]")
    private Set<String> roles = new HashSet<>();

    /**
     * 权限标识集合
     */
    @Schema(description = "权限标识集合，ROOT 角色为 [\"*:*:*\"]", example = "[\"*:*:*\"]")
    private Set<String> permissions = new HashSet<>();

    /**
     * 登录时间（毫秒时间戳）
     */
    @Schema(description = "登录时间，毫秒时间戳", example = "1793287622368")
    private Long loginTime;
}
