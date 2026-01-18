package com.zxy.rpc.loadbalance;

import com.zxy.rpc.dto.RpcReq;

import java.util.List;

/**
 * @author zxy
 * @date 2026/1/2 0:03
 **/
public interface LoadBalance {

    /**
     * 负载均衡，从列表中选择一个
     *
     * @param <T>    列表元素类型
     * @param list   列表
     * @param rpcReq 请求对象
     * @return 选中的对象
     */
    <T> T select(List<T> list, RpcReq rpcReq);
}
