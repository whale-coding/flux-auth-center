package com.flux.system.repository;

import com.flux.system.mapper.RoleMapper;
import com.flux.system.model.entity.Role;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @ClassName RoleRepository
 * @Author njq
 * @Date 2026/10/29
 * @Description 角色、权限、自定义部门的数据访问。
 * mapper 只做单表/两张表查询，权限标识这种需要多跳的在这里组合
 */
@Repository
public class RoleRepository {

    private final RoleMapper roleMapper;

    public RoleRepository(RoleMapper roleMapper) {
        this.roleMapper = roleMapper;
    }

    /**
     * 用户拥有的启用角色
     */
    public List<Role> listEnabledByUserId(Long userId) {
        return roleMapper.selectEnabledByUserId(userId);
    }

    /**
     * 角色集合对应的权限标识：角色→权限ID→权限标识，两段单表查询组合
     */
    public Set<String> listPermissionCodes(Collection<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return Set.of();
        }
        List<Long> permIds = roleMapper.selectPermissionIdsByRoleIds(roleIds);
        if (permIds.isEmpty()) {
            return Set.of();
        }
        return new HashSet<>(roleMapper.selectPermissionCodesByIds(permIds));
    }

    /**
     * 自定义数据范围角色配置的部门集合
     */
    public Set<Long> listCustomDeptIds(Collection<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return Set.of();
        }
        return new HashSet<>(roleMapper.selectCustomDeptIdsByRoleIds(roleIds));
    }
}
