package com.zxy.rpc.factory;

import lombok.SneakyThrows;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zxy
 * @date 2026/1/1 23:40
 **/
public class SingletonFactory {
    private static final Map<Class<?>, Object> INSTANCE_MAP = new ConcurrentHashMap<>();

    private SingletonFactory() {
    }

    @SneakyThrows
    public static <T> T getInstance(Class<T> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("class can't be null");
        }
        // 双重检测单例模式
        if (!INSTANCE_MAP.containsKey(clazz)) {
            synchronized (SingletonFactory.class) {
                if (!INSTANCE_MAP.containsKey(clazz)) {
                    T t = clazz.getConstructor().newInstance();
                    INSTANCE_MAP.put(clazz, t);
                    return t;
                }
            }
        }
        return clazz.cast(INSTANCE_MAP.get(clazz));
    }
}
