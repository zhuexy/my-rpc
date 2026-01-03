package com.zxy.rpc.registry;

import java.net.InetSocketAddress;

/**
 * @author zxy
 * @date 2026/1/1 16:18
 **/
public interface ServiceRegistry {
    /**
     * 注册服务
     *
     * @param rpcServiceName 服务名称(如/rpc/UserService)
     * @param address        服务地址(ip, port)
     */
    void register(String rpcServiceName, InetSocketAddress address);

    /**
     * 清空服务
     */
    void clearAll();
}
