package com.zxy.rpc.registry.impl;

import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.StrUtil;
import com.zxy.rpc.constant.RpcConst;
import com.zxy.rpc.factory.SingletonFactory;
import com.zxy.rpc.registry.ServiceRegistry;
import com.zxy.rpc.registry.zk.ZkClient;
import com.zxy.rpc.util.IPUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

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
        String strAddress = IPUtils.toStringAddress(address);

        String path = RpcConst.ZK_RPC_ROOT_PATH
                + StrUtil.SLASH
                + rpcServiceName
                + StrUtil.SLASH
                + strAddress;

        zkClient.createPersistentNode(path);
    }

    @SneakyThrows
    @Override
    public void clearAll() {
        String host = NetUtil.getLocalhostStr();
        int port = RpcConst.SERVER_PORT;
        zkClient.clearAll(new InetSocketAddress(host, port));
    }
}
