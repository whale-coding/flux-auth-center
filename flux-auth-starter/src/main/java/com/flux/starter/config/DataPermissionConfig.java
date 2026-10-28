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
 * 等 flux-auth-system 把部门表建好后，换成能补全子部门 ID 的实现（deptIds）
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
                    loginUser.getDataScope(), null);
        };
    }
}
