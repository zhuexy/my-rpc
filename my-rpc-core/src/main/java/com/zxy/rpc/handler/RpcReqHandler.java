package com.zxy.rpc.handler;

import com.google.common.util.concurrent.RateLimiter;
import com.zxy.rpc.annotation.RateLimit;
import com.zxy.rpc.dto.RpcReq;
import com.zxy.rpc.exception.RpcException;
import com.zxy.rpc.provider.ServiceProvider;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author zxy
 * @date 2025/12/28 1:48
 **/
@Slf4j
public class RpcReqHandler {

    private final ServiceProvider serviceProvider;

    private static final Map<String, RateLimiter> RATE_LIMITER_MAP = new ConcurrentHashMap<>();

    public RpcReqHandler(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    public Object handle(RpcReq req) {
        Object service = serviceProvider.getService(req.rpcServiceName());

        log.debug("获取到对应服务: {}", service.getClass().getCanonicalName());

        try {
            // 使用反射调用服务
            Method method = service.getClass().getMethod(req.getMethodName(), req.getParamTypes());
            // 判断限流
            RateLimit rateLimit = method.getAnnotation(RateLimit.class);
            if (rateLimit == null) {
                return method.invoke(service, req.getParams());
            }
            RateLimiter rateLimiter = RATE_LIMITER_MAP.computeIfAbsent(
                    req.rpcServiceName() + "." + req.getMethodName(),
                    key -> RateLimiter.create(rateLimit.permitsPerSecond()));

            if (!rateLimiter.tryAcquire(rateLimit.timeout(), TimeUnit.MILLISECONDS)) {
                throw new RpcException("系统繁忙，请稍后重试");
            }
            return method.invoke(service, req.getParams());
        } catch (Exception e) {
            throw new RpcException("调用服务失败: " + e.getMessage());
        }
    }
}
