package com.zxy.rpc.registry.impl;

import cn.hutool.core.util.StrUtil;
import com.zxy.rpc.constant.RpcConst;
import com.zxy.rpc.factory.SingletonFactory;
import com.zxy.rpc.registry.ServiceRegistry;
import com.zxy.rpc.registry.zk.ZkClient;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Objects;

/**
 * @author zxy
 * @date 2026/1/1 23:48
 **/
@Slf4j
public class ZkServiceRegistry implements ServiceRegistry {

    private final ZkClient zkClient;

    public ZkServiceRegistry() {
        this(SingletonFactory.getInstance(ZkClient.class));
    }

    public ZkServiceRegistry(ZkClient zkClient) {
        this.zkClient = zkClient;
    }

    @Override
    public void register(String rpcServiceName, InetSocketAddress address) {
        log.info("注册服务, rpcServiceName {}, address: {}", rpcServiceName, address);
        String hostName = address.getAddress().getHostAddress();
        if (Objects.equals(hostName, "localhost")) {
            hostName = "127.0.0.1";
        }

        String path = RpcConst.ZK_RPC_ROOT_PATH
                + StrUtil.SLASH
                + rpcServiceName
                + StrUtil.SLASH
                + hostName
                + StrUtil.COLON
                + address.getPort();

        zkClient.createPersistentNode(path);
    }
}
