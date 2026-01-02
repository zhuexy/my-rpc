package com.zxy.rpc.util;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zxy
 * @date 2025/12/28 16:30
 **/
@Slf4j
public class ShutdownHookUtils {

    public static void clearAll() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("system shutdown, shutdown all thread pool");
            ThreadPoolUtil.shutdownAll();
        }));
    }

}
