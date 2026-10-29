package com.flux.system.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.flux.common.mybatis.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @ClassName SysDept
 * @Author njq
 * @Date 2026/10/29
 * @Description 部门表 sys_dept
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dept")
public class SysDept extends BaseEntity {

    /**
     * 部门ID
     */
    private Long id;

    /**
     * 部门名称
     */
    private String name;

    /**
     * 父部门ID，0 为顶级
     */
    private Long parentId;

    /**
     * 树节点路径，如 0,1,12
     */
    private String treePath;

    /**
     * 显示顺序
     */
    private Integer sort;

    /**
     * 状态(1:正常;0:禁用)
     */
    private Integer status;
}
