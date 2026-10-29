package com.flux.system.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.flux.common.mybatis.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @ClassName SysPermission
 * @Author njq
 * @Date 2026/10/29
 * @Description 接口/按钮权限表 sys_permission
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_permission")
public class SysPermission extends BaseEntity {

    /**
     * 权限ID
     */
    private Long id;

    /**
     * 归属菜单ID，允许为空
     */
    private Long menuId;

    /**
     * 权限标识，如 system:user:list
     */
    private String permCode;

    /**
     * 接口路径（网关鉴权用，可为空）
     */
    private String apiPath;

    /**
     * 请求方式（网关鉴权用，可为空）
     */
    private String apiMethod;

    /**
     * 权限名称
     */
    private String permName;

    /**
     * 权限类型(1:接口权限;2:按钮权限)
     */
    private Integer type;

    /**
     * 权限状态(1:正常;0:停用)
     */
    private Integer status;

    /**
     * 排序
     */
    private Integer sort;
}
