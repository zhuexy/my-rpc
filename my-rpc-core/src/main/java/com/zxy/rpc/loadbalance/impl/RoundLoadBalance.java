package com.zxy.rpc.loadbalance.impl;

import com.zxy.rpc.dto.RpcReq;
import com.zxy.rpc.loadbalance.LoadBalance;

import java.util.List;

/**
 * @author zxy
 * @date 2026/1/16 20:52
 **/
public class RoundLoadBalance implements LoadBalance {

    private int index = 0;

    @Override
    public <T> T select(List<T> list, RpcReq rpcReq) {
        return list.get(index++ % list.size());
    }
}
