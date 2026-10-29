package com.flux.core.service.impl;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.secure.BCrypt;
import com.flux.common.core.constant.SystemConstants;
import com.flux.common.core.enums.StatusEnum;
import com.flux.common.core.result.ResultCode;
import com.flux.common.mybatis.enums.DataScopeEnum;
import com.flux.common.satoken.helper.LoginHelper;
import com.flux.common.satoken.model.LoginUser;
import com.flux.common.satoken.service.LoginService;
import com.flux.common.web.exception.BizException;
import com.flux.core.model.form.LoginForm;
import com.flux.core.model.vo.LoginVO;
import com.flux.core.service.AuthService;
import com.flux.core.support.DataScopeMerger;
import com.flux.system.model.entity.Role;
import com.flux.system.model.entity.User;
import com.flux.system.service.AuthDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ClassName AuthServiceImpl
 * @Author njq
 * @Date 2026/10/29
 * @Description 登录流程：查用户 -> 校验密码/状态 -> 组装 LoginUser -> 签发 token。
 * 角色、权限、部门数据由 flux-auth-system 提供，本类只负责认证与登录态
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthDataService authDataService;
    private final LoginService loginService;

    @Override
    public LoginVO login(LoginForm form) {
        User user = authDataService.getByUsername(form.getUsername());

        // 用户不存在和密码错误返回同一个错误码，不暴露账号是否存在
        if (user == null || !matches(form.getPassword(), user.getPassword())) {
            throw new BizException(ResultCode.USERNAME_OR_PASSWORD_ERROR);
        }
        if (!StatusEnum.ENABLE.getValue().equals(user.getStatus())) {
            throw new BizException(ResultCode.USER_ACCOUNT_DISABLED);
        }

        LoginUser loginUser = buildLoginUser(user);
        loginService.login(loginUser);

        return new LoginVO(SaManager.getConfig().getTokenName(),
                LoginHelper.getTokenValue(),
                SaManager.getConfig().getTimeout());
    }

    @Override
    public void logout() {
        loginService.logout();
    }

    /**
     * 组装写入 token-session 的登录用户信息。
     * ROOT 角色直接给通配权限和全部数据范围，不再查权限表
     */
    private LoginUser buildLoginUser(User user) {
        List<Role> roles = authDataService.listEnabledRoles(user.getId());
        Set<String> roleCodes = roles.stream()
                .map(Role::getCode)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Set<Long> roleIds = roles.stream()
                .map(Role::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        boolean root = roleCodes.contains(SystemConstants.ROOT_ROLE_CODE);

        DataScopeEnum dataScope = root
                ? DataScopeEnum.ALL
                : DataScopeMerger.merge(roles.stream().map(Role::getDataScope).toList());
        Set<String> permissions = root
                ? Set.of(SystemConstants.ALL_PERMISSION)
                : authDataService.listPermissionCodes(roleIds);
        Set<Long> dataScopeDeptIds = switch (dataScope) {
            case DEPT_AND_CHILD -> authDataService.listSelfAndChildDeptIds(user.getDeptId());
            case CUSTOM -> authDataService.listCustomDeptIds(
                    roles.stream()
                            .filter(role -> DataScopeEnum.CUSTOM.getValue().equals(role.getDataScope()))
                            .map(Role::getId)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toSet()));
            default -> Set.of();
        };

        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(user.getId());
        loginUser.setUsername(user.getUsername());
        loginUser.setNickname(user.getNickname());
        loginUser.setAvatar(user.getAvatar());
        loginUser.setDeptId(user.getDeptId());
        loginUser.setDeptName(authDataService.getDeptName(user.getDeptId()));
        loginUser.setDataScope(dataScope.getValue());
        loginUser.setDataScopeDeptIds(dataScopeDeptIds);
        loginUser.setRoles(roleCodes);
        loginUser.setPermissions(permissions);
        loginUser.setLoginTime(System.currentTimeMillis());
        return loginUser;
    }

    /**
     * BCrypt 校验。库里没有密码、哈希格式不对都按失败处理，不让异常冒到接口层
     */
    private boolean matches(String rawPassword, String encodedPassword) {
        if (!StringUtils.hasText(rawPassword) || !StringUtils.hasText(encodedPassword)) {
            return false;
        }
        try {
            return BCrypt.checkpw(rawPassword, encodedPassword);
        } catch (RuntimeException e) {
            log.warn("密码校验失败，可能是密码哈希格式不正确", e);
            return false;
        }
    }
}
