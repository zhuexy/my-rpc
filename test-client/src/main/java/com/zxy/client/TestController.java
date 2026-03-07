package com.zxy.client;

import com.zxy.api.UserService;
import com.zxy.rpc.annotation.RpcReference;

/**
 * @author zxy
 * @date 2026/3/5 15:02
 **/
public class TestController {

    @RpcReference(version = "1", group = "test")
    private UserService userService;

    public void test() {
        System.out.println(userService.getUser(1));
    }
}
