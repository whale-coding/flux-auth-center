package com.flux.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.flux.system.model.entity.Role;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * @ClassName RoleMapper
 * @Author njq
 * @Date 2026/10/29
 * @Description 角色表 Mapper
 */
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 用户拥有的启用角色（sys_role + sys_user_role）
     */
    List<Role> selectEnabledByUserId(@Param("userId") Long userId);

    /**
     * 角色对应的权限 ID（sys_role_permission）
     */
    List<Long> selectPermissionIdsByRoleIds(@Param("roleIds") Collection<Long> roleIds);

    /**
     * 权限 ID 对应的权限标识（sys_permission）
     */
    List<String> selectPermissionCodesByIds(@Param("permIds") Collection<Long> permIds);

    /**
     * 自定义数据范围角色配置的部门 ID（sys_role_dept）
     */
    List<Long> selectCustomDeptIdsByRoleIds(@Param("roleIds") Collection<Long> roleIds);
}
