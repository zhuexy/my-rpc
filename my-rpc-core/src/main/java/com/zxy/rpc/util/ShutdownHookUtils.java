package com.zxy.rpc.util;

import com.zxy.rpc.factory.SingletonFactory;
import com.zxy.rpc.registry.impl.ZkServiceRegistry;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zxy
 * @date 2025/12/28 16:30
 **/
@Slf4j
public class ShutdownHookUtils {

    // 添加关闭钩子
    public static void clearAll() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // 释放zk资源
            log.info("release zk resource");
            ZkServiceRegistry zkServiceRegistry = SingletonFactory.getInstance(ZkServiceRegistry.class);
            zkServiceRegistry.clearAll();
            // 关闭所有线程池
            log.info("system shutdown, shutdown all thread pool");
            ThreadPoolUtil.shutdownAll();
        }));
    }

}
