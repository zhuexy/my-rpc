package com.zxy.rpc.util;

import com.zxy.rpc.factory.SingletonFactory;
import com.zxy.rpc.proxy.RpcClientProxy;
import com.zxy.rpc.tansmission.RpcClient;
import com.zxy.rpc.tansmission.socket.SocketRpcClient;

/**
 * @author zxy
 * @date 2025/12/29 1:27
 **/
public class ProxyUtils {

    private static final RpcClient RPC_CLIENT = SingletonFactory.getInstance(SocketRpcClient.class);

    private static final RpcClientProxy PRC_CLIENT_PROXY = new RpcClientProxy(RPC_CLIENT);

    public static <T> T getProxy(Class<T> clazz) {
        return PRC_CLIENT_PROXY.getProxy(clazz);
    }

}
