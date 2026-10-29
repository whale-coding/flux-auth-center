package com.flux.system.repository;

import com.flux.system.mapper.LoginLogMapper;
import com.flux.system.model.entity.LoginLog;
import org.springframework.stereotype.Repository;

/**
 * @ClassName LoginLogRepository
 * @Author njq
 * @Date 2026/10/29
 * @Description 登录日志数据访问
 */
@Repository
public class LoginLogRepository {

    private final LoginLogMapper loginLogMapper;

    public LoginLogRepository(LoginLogMapper loginLogMapper) {
        this.loginLogMapper = loginLogMapper;
    }

    public void save(LoginLog record) {
        loginLogMapper.insert(record);
    }
}
