package com.zxy.rpc.breaker;

import com.zxy.rpc.annotation.Breaker;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zxy
 * @date 2026/1/16 0:40
 **/
public class CircuitBreakerManager {
    private static final Map<String, CircuitBreaker> BREAKER_MAP = new ConcurrentHashMap<>();

    public static CircuitBreaker get(String key, Breaker breaker) {
        return BREAKER_MAP.computeIfAbsent(key, k -> new CircuitBreaker(
                breaker.failRateThreshold(),
                breaker.minRequestCount(),
                breaker.successRateThreshold(),
                breaker.windowSize()
        ));
    }
}
