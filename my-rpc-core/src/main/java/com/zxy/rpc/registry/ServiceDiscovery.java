package com.zxy.rpc.registry;

import com.zxy.rpc.dto.RpcReq;

import java.net.InetSocketAddress;

/**
 * @Author zxy
 * @Date 2026/1/1 16:20
 **/
public interface ServiceDiscovery {
    /**
     * 发现服务
     *
     * @param rpcReq 服务请求
     * @return 服务地址
     */
    InetSocketAddress lookupService(RpcReq rpcReq);
}
