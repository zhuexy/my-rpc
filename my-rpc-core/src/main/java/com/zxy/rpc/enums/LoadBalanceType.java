package com.zxy.rpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author zxy
 * @date 2026/1/18 20:30
 **/
@ToString
@Getter
@AllArgsConstructor
public enum LoadBalanceType {
    RANDOM("random"),
    ROUND("round"),
    CONSISTENT_HASH("consistentHash"),
    ;
    private final String desc;

    public static LoadBalanceType from(String desc) {
        for (LoadBalanceType type : LoadBalanceType.values()) {
            if (type.getDesc().equalsIgnoreCase(desc)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unsupported LoadBalance type: " + desc);
    }
}
