package com.zxy.rpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;

/**
 * @author zxy
 * @date 2026/1/11 15:35
 **/
@ToString
@Getter
@AllArgsConstructor
public enum SerializerType {
    CUSTOM((byte) -1, "custom"),
    KRYO((byte) 0, "kryo"),
    JSON((byte) 1, "json"),
    HESSIAN((byte) 2, "hessian"),
    PROTOSTUFF((byte) 3, "protostuff");

    private final byte code;
    private final String desc;

    public static SerializerType from(byte code) {
        return Arrays.stream(values())
                .filter(o -> o.getCode() == code)
                .findFirst()
                .orElse(CUSTOM);
    }

    public static SerializerType from(String desc) {
        return Arrays.stream(values())
                .filter(o -> o.getDesc().equalsIgnoreCase(desc))
                .findFirst()
                .orElse(CUSTOM);
    }
}
