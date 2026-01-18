package com.zxy.rpc.loadbalance.impl;

import com.google.common.hash.Hashing;
import com.zxy.rpc.dto.RpcReq;
import com.zxy.rpc.loadbalance.LoadBalance;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author zxy
 * @date 2026/1/16 20:58
 **/
public class ConsistentHashLoadBalance implements LoadBalance {
    @Override
    public <T> T select(List<T> list, RpcReq rpcReq) {
        String key = rpcReq.rpcServiceName() + rpcReq.getMethodName();
        long hashCode = Hashing.murmur3_128().hashString(key, StandardCharsets.UTF_8).asLong();
        int index = Hashing.consistentHash(hashCode, list.size());
        return list.get(index);
    }
}
