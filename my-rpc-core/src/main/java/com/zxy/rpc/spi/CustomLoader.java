package com.zxy.rpc.spi;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zxy
 * @date 2026/1/16 22:29
 **/
public class CustomLoader<T> {

    private static final String BASE_PATH = "META-INF/my-rpc/";
    // 一个接口对应一个CustomLoader
    private static final Map<Class<?>, CustomLoader<?>> LOADERS = new ConcurrentHashMap<>();
    // 泛型类型
    private final Class<T> type;
    // 名称 -> 实现类
    private final Map<String, Class<? extends T>> classCache = new ConcurrentHashMap<>();
    // 名称 -> 实例对象
    private final Map<String, T> instanceCache = new ConcurrentHashMap<>();

    private CustomLoader(Class<T> type) {
        this.type = type;
    }

    @SuppressWarnings("unchecked")
    public static <T> CustomLoader<T> getLoader(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("Spi type is null");
        }
        if (!type.isInterface()) {
            throw new IllegalArgumentException("Spi type " + type.getName() + " is not interface");
        }
        return (CustomLoader<T>) LOADERS.computeIfAbsent(type, key -> new CustomLoader<>(type));
    }

    public T get(String name) {
        if (StrUtil.isEmpty(name)) {
            throw new IllegalArgumentException("Spi name is empty");
        }
        return instanceCache.computeIfAbsent(name, key -> createObj(name));
    }

    @SneakyThrows
    private T createObj(String name) {
        if (CollUtil.isEmpty(classCache)) {
            loadClasses();
        }
        Class<? extends T> clazz = classCache.get(name);
        if (clazz == null) {
            throw new IllegalArgumentException("No implementation found for " + type.getName() + " with name: " + name);
        }
        return clazz.newInstance();
    }

    @SneakyThrows
    private void loadClasses() {
        String path = BASE_PATH + type.getName();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> urls = classLoader.getResources(path);
        if (CollUtil.isEmpty(urls)) {
            throw new RuntimeException("No implementation found for " + type.getName());
        }
        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                    url.openStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    handleLine(line, classLoader);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    private void handleLine(String line, ClassLoader classLoader) {
        line = line.trim();
        if (StrUtil.isEmpty(line)) {
            return;
        }
        String[] split = line.split("=");
        if (split.length != 2) {
            throw new IllegalArgumentException("Invalid spi config: " + line);
        }
        String spiName = split[0].trim();
        String className = split[1].trim();
        Class<?> clazz = classLoader.loadClass(className);
        // 若type不是clazz的父类或接口，则抛出异常
        if (!type.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException("Class " + className + " does not implement " + type.getName());
        }
        classCache.put(spiName, (Class<? extends T>) clazz);
    }
}
