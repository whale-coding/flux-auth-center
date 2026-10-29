package com.flux.system.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.flux.common.mybatis.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @ClassName Role
 * @Author njq
 * @Date 2026/10/29
 * @Description 角色表 sys_role
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class Role extends BaseEntity {

    /**
     * 角色ID
     */
    private Long id;

    /**
     * 角色名称
     */
    private String name;

    /**
     * 角色编码，如 ROOT
     */
    private String code;

    /**
     * 显示顺序
     */
    private Integer sort;

    /**
     * 角色状态(1:正常;0:停用)
     */
    private Integer status;

    /**
     * 数据权限(0:所有数据;1:部门及子部门;2:本部门;3:本人;4:自定义部门)
     */
    private Integer dataScope;
}
