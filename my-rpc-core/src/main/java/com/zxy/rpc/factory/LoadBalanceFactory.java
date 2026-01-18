package com.zxy.rpc.factory;

import com.zxy.rpc.enums.LoadBalanceType;
import com.zxy.rpc.loadbalance.LoadBalance;
import com.zxy.rpc.loadbalance.impl.ConsistentHashLoadBalance;
import com.zxy.rpc.loadbalance.impl.RandomLoadBalance;
import com.zxy.rpc.loadbalance.impl.RoundLoadBalance;

import java.util.EnumMap;
import java.util.Map;

/**
 * @author zxy
 * @date 2026/1/18 20:28
 **/
public class LoadBalanceFactory {

    private static final Map<LoadBalanceType, LoadBalance> LOAD_BALANCE_MAP = new EnumMap<>(LoadBalanceType.class);

    private LoadBalanceFactory() {
    }

    public static LoadBalance get(LoadBalanceType type) {
        if (type == null) {
            throw new IllegalArgumentException("LoadBalance type is null");
        }

        if (LOAD_BALANCE_MAP.containsKey(type)) {
            return LOAD_BALANCE_MAP.get(type);
        }

        synchronized (LoadBalanceFactory.class) {
            if (LOAD_BALANCE_MAP.containsKey(type)) {
                return LOAD_BALANCE_MAP.get(type);
            }

            LoadBalance loadBalance;
            switch (type) {
                case RANDOM:
                    loadBalance = SingletonFactory.getInstance(RandomLoadBalance.class);
                    break;
                case ROUND:
                    loadBalance = SingletonFactory.getInstance(RoundLoadBalance.class);
                    break;
                case CONSISTENT_HASH:
                    loadBalance = SingletonFactory.getInstance(ConsistentHashLoadBalance.class);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported LoadBalance type: " + type);
            }

            LOAD_BALANCE_MAP.put(type, loadBalance);
            return loadBalance;
        }
    }
}
