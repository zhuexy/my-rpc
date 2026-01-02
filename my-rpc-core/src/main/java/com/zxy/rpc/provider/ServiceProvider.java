package com.zxy.rpc.provider;

import com.zxy.rpc.config.RpcServiceConfig;

/**
 * @author zxy
 * @date 2025/12/27 17:43
 **/
public interface ServiceProvider {

    void publishService(RpcServiceConfig config);

    Object getService(String rpcServiceName);
}
