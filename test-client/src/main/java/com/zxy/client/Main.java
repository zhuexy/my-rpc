package com.zxy.client;

import com.zxy.api.User;
import com.zxy.api.UserService;
import com.zxy.rpc.util.ProxyUtils;
import com.zxy.rpc.util.ThreadPoolUtil;

import java.util.concurrent.ExecutorService;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        // 使用动态代理，让调用远程服务和调用本地方法一样
        UserService userService = ProxyUtils.getProxy(UserService.class);
        ExecutorService executorService = ThreadPoolUtil.createThreadPool(20, "test-pool");
        for (int i = 0; i < 20; i++) {
            executorService.submit(() -> {
                User user = userService.getUser(1);
                System.out.println(user);
            });
        }
    }
}