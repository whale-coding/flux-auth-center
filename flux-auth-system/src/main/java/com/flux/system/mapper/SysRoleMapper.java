package com.flux.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.flux.system.model.entity.SysRole;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @ClassName SysRoleMapper
 * @Author njq
 * @Date 2026/10/29
 * @Description 角色表 Mapper。登录时的角色/权限查询都在这里，手写 SQL 必须自己带 deleted = 0
 */
public interface SysRoleMapper extends BaseMapper<SysRole> {

    /**
     * 用户拥有的启用角色
     */
    @Select("""
            select r.id, r.name, r.code, r.sort, r.status, r.data_scope
            from sys_role r
            join sys_user_role ur on ur.role_id = r.id
            where ur.user_id = #{userId}
              and r.status = 1
              and r.deleted = 0
            order by r.sort
            """)
    List<SysRole> selectEnabledByUserId(@Param("userId") Long userId);

    /**
     * 用户拥有的权限标识（启用角色 + 启用权限）
     */
    @Select("""
            select distinct p.perm_code
            from sys_permission p
            join sys_role_permission rp on rp.perm_id = p.id
            join sys_user_role ur on ur.role_id = rp.role_id
            join sys_role r on r.id = ur.role_id
            where ur.user_id = #{userId}
              and r.status = 1
              and r.deleted = 0
              and p.status = 1
              and p.deleted = 0
            """)
    List<String> selectPermissionCodesByUserId(@Param("userId") Long userId);

    /**
     * 用户通过「自定义数据范围」角色配置的部门集合
     */
    @Select("""
            select distinct rd.dept_id
            from sys_role_dept rd
            join sys_role r on r.id = rd.role_id
            join sys_user_role ur on ur.role_id = r.id
            where ur.user_id = #{userId}
              and r.status = 1
              and r.deleted = 0
              and r.data_scope = 4
            """)
    List<Long> selectCustomDeptIdsByUserId(@Param("userId") Long userId);
}
