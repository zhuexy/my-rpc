package com.zxy.rpc.tansmission;

import com.zxy.rpc.config.RpcServiceConfig;

/**
 * @Author zxy
 * @Date 2025/12/27 16:46
 **/
public interface RpcServer {

    void start();

    void publishService(RpcServiceConfig config);

}
