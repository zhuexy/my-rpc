package com.zxy.rpc.util;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.*;

/**
 * @author zxy
 * @date 2025/12/28 15:57
 **/
@Slf4j
public final class ThreadPoolUtil {

    private static final Map<String, ExecutorService> THREAD_POOL_CACHE = new ConcurrentHashMap<>();
    // cpu核数
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    // cpu密集型线程数
    private static final int CPU_INTENSIVE_THREAD_COUNT = CPU_COUNT + 1;
    // io密集型线程数
    private static final int IO_INTENSIVE_THREAD_COUNT = CPU_COUNT * 2;
    // 默认线程池参数
    private static final long DEFAULT_KEEP_ALIVE_TIME = 60L;
    private static final int DEFAULT_BLOCKING_QUEUE_CAPACITY = 128;

    public static ExecutorService crateCpuIntensiveThreadPool(String poolName) {
        return crateThreadPool(CPU_INTENSIVE_THREAD_COUNT, poolName);
    }

    public static ExecutorService crateIoIntensiveThreadPool(String poolName) {
        return crateThreadPool(IO_INTENSIVE_THREAD_COUNT, poolName);
    }

    public static ExecutorService crateThreadPool(int corePoolSize, String poolName) {
        return crateThreadPool(corePoolSize, corePoolSize, poolName);
    }

    public static ExecutorService crateThreadPool(
            int corePoolSize,
            int maxPoolSize,
            String poolName) {
        return crateThreadPool(corePoolSize, maxPoolSize, DEFAULT_KEEP_ALIVE_TIME, poolName, false);
    }

    public static ExecutorService crateThreadPool(
            int corePoolSize,
            int maxPoolSize,
            long keepAliveTime,
            String poolName,
            boolean isDaemon) {
        if (THREAD_POOL_CACHE.containsKey(poolName)) {
            return THREAD_POOL_CACHE.get(poolName);
        }
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                keepAliveTime,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(DEFAULT_BLOCKING_QUEUE_CAPACITY),
                createThreadFactory(poolName, isDaemon)
        );
        THREAD_POOL_CACHE.put(poolName, threadPoolExecutor);
        return threadPoolExecutor;
    }

    private static ThreadFactory createThreadFactory(String poolName, boolean isDaemon) {
        ThreadFactoryBuilder threadFactoryBuilder = new ThreadFactoryBuilder().setDaemon(isDaemon);

        if (StrUtil.isEmpty(poolName)) {
            return threadFactoryBuilder.build();
        }

        return threadFactoryBuilder
                .setNamePrefix(poolName)
                .build();
    }

    public static void shutdownAll() {
        // 使用并行流关闭线程池
        THREAD_POOL_CACHE.entrySet().parallelStream().forEach(entry -> {
            String poolName = entry.getKey();
            ExecutorService executorService = entry.getValue();

            executorService.shutdown();
            log.info("shutdown thread pool: {}", poolName);

            try {
                // 如果等待10秒还没关闭，则强制关闭
                if (executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                    log.info("thread pool: {} shutdown success", poolName);
                } else {
                    log.info("thread pool: {} shutdown timeout, force shutdown", poolName);
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                log.error("thread pool: {} shutdown error", poolName, e);
                executorService.shutdownNow();
            }
        });
    }
}
