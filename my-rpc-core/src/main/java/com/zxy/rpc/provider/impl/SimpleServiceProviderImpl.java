package com.zxy.rpc.provider.impl;

import cn.hutool.core.collection.CollUtil;
import com.zxy.rpc.config.RpcServiceConfig;
import com.zxy.rpc.provider.ServiceProvider;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zxy
 * @date 2025/12/28 0:56
 **/
@Slf4j
public class SimpleServiceProviderImpl implements ServiceProvider {

    private final static Map<String, Object> SERVICE_CACHE = new HashMap<>();

    @Override
    public void publishService(RpcServiceConfig config) {
        List<String> rpcServiceNames = config.rpcServiceNames();

        if (CollUtil.isEmpty(rpcServiceNames)) {
            throw new RuntimeException("该服务没有实现");
        }

        log.debug("发布服务：{}", rpcServiceNames);

        rpcServiceNames.forEach(name -> {
            SERVICE_CACHE.put(name, config.getService());
        });
    }

    @Override
    public Object getService(String rpcServiceName) {
        if (!SERVICE_CACHE.containsKey(rpcServiceName)) {
            throw new RuntimeException("找不到对应服务" + rpcServiceName);
        }
        return SERVICE_CACHE.get(rpcServiceName);
    }
}
