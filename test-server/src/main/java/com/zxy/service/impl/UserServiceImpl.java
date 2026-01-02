package com.zxy.service.impl;

import com.zxy.api.User;
import com.zxy.api.UserService;

/**
 * @author zxy
 * @date 2025/12/27 16:27
 **/
public class UserServiceImpl implements UserService {
    @Override
    public User getUser(Integer id) {
        return User.builder()
                .id(id)
                .name("zxy")
                .age(18)
                .build();
    }
}
