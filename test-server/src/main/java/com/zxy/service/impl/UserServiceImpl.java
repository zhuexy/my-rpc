package com.zxy.service.impl;

import com.zxy.api.User;
import com.zxy.api.UserService;
import com.zxy.rpc.annotation.RateLimit;

/**
 * @author zxy
 * @date 2025/12/27 16:27
 **/
public class UserServiceImpl implements UserService {
    @RateLimit(permitsPerSecond = 5, timeout = 0)
    @Override
    public User getUser(Integer id) {
        return User.builder()
                .id(id)
                .name("zxy")
                .age(18)
                .build();
    }
}
