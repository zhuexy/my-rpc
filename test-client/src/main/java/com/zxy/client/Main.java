package com.zxy.client;

import com.zxy.api.UserService;
import com.zxy.rpc.util.ProxyUtils;

public class Main {
    public static void main(String[] args) {
        // 使用动态代理，让调用远程服务和调用本地方法一样
        UserService userService = ProxyUtils.getProxy(UserService.class);
        for (int i = 0; i < 10; i++) {
            System.out.println(userService.getUser(i));
        }
    }
}