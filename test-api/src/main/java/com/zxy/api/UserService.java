package com.zxy.api;

import com.zxy.rpc.annotation.Breaker;

/**
 * @author zxy
 * @date 2025/12/27 16:19
 **/
public interface UserService {
    @Breaker(windowSize = 10000L)
    User getUser(Integer id);

}
