package com.flux.starter.config;

import com.flux.common.mybatis.handler.DataPermissionProvider;
import com.flux.common.mybatis.model.UserDataScope;
import com.flux.common.satoken.helper.LoginHelper;
import com.flux.common.satoken.model.LoginUser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName DataPermissionConfig
 * @Author njq
 * @Date 2026/10/28
 * @Description 数据权限数据源：直接取登录时写进 token-session 的 LoginUser。
 * 部门集合在登录时已经算好（DEPT_AND_CHILD 是部门子树，CUSTOM 是 sys_role_dept），
 * 这里不再查库，避免每个请求都扫一遍部门表
 */
@Configuration
public class DataPermissionConfig {

    @Bean
    public DataPermissionProvider dataPermissionProvider() {
        return () -> {
            LoginUser loginUser = LoginHelper.getLoginUser();
            if (loginUser == null) {
                return null;
            }
            return new UserDataScope(loginUser.getUserId(), loginUser.getDeptId(),
                    loginUser.getDataScope(), loginUser.getDataScopeDeptIds());
        };
    }
}
