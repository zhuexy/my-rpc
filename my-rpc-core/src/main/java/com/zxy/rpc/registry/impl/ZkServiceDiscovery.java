package com.zxy.rpc.registry.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.zxy.rpc.config.RpcConfig;
import com.zxy.rpc.constant.RpcConst;
import com.zxy.rpc.dto.RpcReq;
import com.zxy.rpc.enums.LoadBalanceType;
import com.zxy.rpc.factory.LoadBalanceFactory;
import com.zxy.rpc.factory.SingletonFactory;
import com.zxy.rpc.loadbalance.LoadBalance;
import com.zxy.rpc.registry.ServiceDiscovery;
import com.zxy.rpc.registry.zk.ZkClient;
import com.zxy.rpc.util.IPUtils;
import com.zxy.rpc.util.RpcConfigUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author zxy
 * @date 2026/1/2 0:00
 **/
@Slf4j
public class ZkServiceDiscovery implements ServiceDiscovery {
    private final ZkClient zkClient;

    private final LoadBalance loadBalance;

    public ZkServiceDiscovery() {
        this.zkClient = SingletonFactory.getInstance(ZkClient.class);
        RpcConfig rpcConfig = RpcConfigUtils.getRpcConfig();
        LoadBalanceType type = LoadBalanceType.from(rpcConfig.getLoadBalance());
        this.loadBalance = LoadBalanceFactory.get(type);
    }

    public ZkServiceDiscovery(ZkClient zkClient, LoadBalance loadBalance) {
        this.zkClient = zkClient;
        this.loadBalance = loadBalance;
    }

    @Override
    public InetSocketAddress lookupService(RpcReq rpcReq) {
        String rpcServiceName = rpcReq.rpcServiceName();
        String servicePath = RpcConst.ZK_RPC_ROOT_PATH + StrUtil.SLASH + rpcServiceName;
        List<String> children = zkClient.getChildren(servicePath);
        if (CollUtil.isEmpty(children)) {
            throw new RuntimeException("找不到对应的服务: " + rpcServiceName);
        }
        String address = loadBalance.select(children, rpcReq);
        return IPUtils.toInetsocketAddress(address);
    }
}
