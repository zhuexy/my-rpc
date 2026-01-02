package com.zxy.rpc.provider.impl;

import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.StrUtil;
import com.zxy.rpc.config.RpcServiceConfig;
import com.zxy.rpc.constant.RpcConst;
import com.zxy.rpc.factory.SingletonFactory;
import com.zxy.rpc.provider.ServiceProvider;
import com.zxy.rpc.registry.ServiceRegistry;
import com.zxy.rpc.registry.impl.ZkServiceRegistry;
import lombok.AllArgsConstructor;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zxy
 * @date 2026/1/2 0:14
 **/
@AllArgsConstructor
public class ZkServiceProvider implements ServiceProvider {
    private final ServiceRegistry serviceRegistry;
    private static final Map<String, Object> SERVICE_CACHE = new HashMap<>();

    public ZkServiceProvider() {
        this(SingletonFactory.getInstance(ZkServiceRegistry.class));
    }

    @Override
    public void publishService(RpcServiceConfig config) {
        config.rpcServiceNames().forEach(serviceName -> {
            // 获取本机ip地址
            String host = NetUtil.getLocalhostStr();
            // 注册服务
            serviceRegistry.register(serviceName, new InetSocketAddress(host, RpcConst.SERVER_PORT));
            // 加入缓存
            SERVICE_CACHE.put(serviceName, config.getService());
        });
    }

    @Override
    public Object getService(String rpcServiceName) {
        if (StrUtil.isEmpty(rpcServiceName)) {
            throw new IllegalArgumentException("rpcServiceName can not be null");
        }
        if (!SERVICE_CACHE.containsKey(rpcServiceName)) {
            throw new RuntimeException("找不到对应服务" + rpcServiceName);
        }
        return SERVICE_CACHE.get(rpcServiceName);
    }
}
