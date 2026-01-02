package com.zxy.rpc.handler;

import com.zxy.rpc.dto.RpcReq;
import com.zxy.rpc.provider.ServiceProvider;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * @author zxy
 * @date 2025/12/28 1:48
 **/
@Slf4j
public class RpcReqHandler {

    private final ServiceProvider serviceProvider;

    public RpcReqHandler(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    @SneakyThrows
    public Object invoke(RpcReq req) {
        Object service = serviceProvider.getService(req.rpcServiceName());

        log.debug("获取到对应服务: {}", service.getClass().getCanonicalName());

        // 使用反射调用服务
        Method method = service.getClass().getMethod(req.getMethodName(), req.getParamTypes());
        return method.invoke(service, req.getParams());
    }
}
