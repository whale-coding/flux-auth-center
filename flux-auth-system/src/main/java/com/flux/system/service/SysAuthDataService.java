package com.flux.system.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.flux.system.mapper.SysDeptMapper;
import com.flux.system.mapper.SysRoleMapper;
import com.flux.system.mapper.SysUserMapper;
import com.flux.system.model.entity.SysDept;
import com.flux.system.model.entity.SysRole;
import com.flux.system.model.entity.SysUser;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @ClassName SysAuthDataService
 * @Author njq
 * @Date 2026/10/29
 * @Description 认证链路的数据查询入口：用户、角色、权限、部门。
 * 只做查询，不做登录逻辑；登录流程在 flux-auth-core，本模块不依赖 core
 */
@Service
public class SysAuthDataService {

    private final SysUserMapper userMapper;
    private final SysRoleMapper roleMapper;
    private final SysDeptMapper deptMapper;

    public SysAuthDataService(SysUserMapper userMapper, SysRoleMapper roleMapper, SysDeptMapper deptMapper) {
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.deptMapper = deptMapper;
    }

    /**
     * 按用户名查用户（逻辑删除由 MP 自动追加 deleted = 0），不存在返回 null
     */
    public SysUser getByUsername(String username) {
        return userMapper.selectOne(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUsername, username));
    }

    /**
     * 用户拥有的启用角色
     */
    public List<SysRole> listEnabledRoles(Long userId) {
        return roleMapper.selectEnabledByUserId(userId);
    }

    /**
     * 用户拥有的权限标识
     */
    public Set<String> listPermissionCodes(Long userId) {
        return new HashSet<>(roleMapper.selectPermissionCodesByUserId(userId));
    }

    /**
     * 自定义数据范围角色配置的部门集合
     */
    public Set<Long> listCustomDeptIds(Long userId) {
        return new HashSet<>(roleMapper.selectCustomDeptIdsByUserId(userId));
    }

    /**
     * 部门及子部门 ID 集合，部门为空时返回空集合
     */
    public Set<Long> listSelfAndChildDeptIds(Long deptId) {
        if (deptId == null) {
            return Set.of();
        }
        return new HashSet<>(deptMapper.selectSelfAndChildIds(deptId));
    }

    /**
     * 部门名称，不存在返回 null
     */
    public String getDeptName(Long deptId) {
        if (deptId == null) {
            return null;
        }
        SysDept dept = deptMapper.selectById(deptId);
        return dept == null ? null : dept.getName();
    }
}
