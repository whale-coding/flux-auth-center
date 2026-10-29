package com.flux.system.repository;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.flux.system.mapper.UserMapper;
import com.flux.system.model.entity.User;
import org.springframework.stereotype.Repository;

/**
 * @ClassName UserRepository
 * @Author njq
 * @Date 2026/10/29
 * @Description 用户数据访问。service 只依赖本层，不直接接触 Mapper
 */
@Repository
public class UserRepository {

    private final UserMapper userMapper;

    public UserRepository(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /**
     * 按用户名查用户，逻辑删除由 MP 自动追加 deleted = 0，不存在返回 null
     */
    public User getByUsername(String username) {
        return userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, username));
    }
}
