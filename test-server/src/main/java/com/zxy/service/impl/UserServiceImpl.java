package com.zxy.service.impl;

import com.zxy.api.User;
import com.zxy.api.UserService;
import com.zxy.rpc.annotation.RpcService;

/**
 * @author zxy
 * @date 2025/12/27 16:27
 **/
@RpcService
public class UserServiceImpl implements UserService {

    @Override
    public User getUser(Integer id) {
        if (id < 0) {
            throw new IllegalArgumentException("invalid user id");
        }
        return User.builder()
                .id(id)
                .name("zxy")
                .age(18)
                .build();
    }
}
