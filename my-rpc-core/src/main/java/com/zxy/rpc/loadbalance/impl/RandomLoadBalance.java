package com.zxy.rpc.loadbalance.impl;

import cn.hutool.core.util.RandomUtil;
import com.zxy.rpc.loadbalance.LoadBalance;

import java.util.List;

/**
 * @author zxy
 * @date 2026/1/2 0:06
 **/
public class RandomLoadBalance implements LoadBalance {
    @Override
    public <T> T select(List<T> list) {
        return RandomUtil.randomEle(list);
    }
}
