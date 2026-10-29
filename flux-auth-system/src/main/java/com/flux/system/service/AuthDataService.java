package com.flux.system.service;

import com.flux.system.model.entity.Role;
import com.flux.system.model.entity.User;
import com.flux.system.repository.DeptRepository;
import com.flux.system.repository.RoleRepository;
import com.flux.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @ClassName AuthDataService
 * @Author njq
 * @Date 2026/10/29
 * @Description 认证链路的数据查询入口：用户、角色、权限、部门。
 * 只依赖 repository 层，不做登录逻辑；登录流程在 flux-auth-core，本模块不依赖 core
 */
@Service
public class AuthDataService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DeptRepository deptRepository;

    public AuthDataService(UserRepository userRepository,
                           RoleRepository roleRepository,
                           DeptRepository deptRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.deptRepository = deptRepository;
    }

    /**
     * 按用户名查用户，不存在返回 null
     */
    public User getByUsername(String username) {
        return userRepository.getByUsername(username);
    }

    /**
     * 用户拥有的启用角色
     */
    public List<Role> listEnabledRoles(Long userId) {
        return roleRepository.listEnabledByUserId(userId);
    }

    /**
     * 角色集合对应的权限标识
     */
    public Set<String> listPermissionCodes(Collection<Long> roleIds) {
        return roleRepository.listPermissionCodes(roleIds);
    }

    /**
     * 自定义数据范围角色配置的部门集合
     */
    public Set<Long> listCustomDeptIds(Collection<Long> roleIds) {
        return roleRepository.listCustomDeptIds(roleIds);
    }

    /**
     * 部门及子部门 ID 集合
     */
    public Set<Long> listSelfAndChildDeptIds(Long deptId) {
        return deptRepository.listSelfAndChildIds(deptId);
    }

    /**
     * 部门名称，不存在返回 null
     */
    public String getDeptName(Long deptId) {
        return deptRepository.getDeptName(deptId);
    }
}
