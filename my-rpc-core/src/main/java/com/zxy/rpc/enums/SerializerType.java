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
    KRYO((byte) 0, "kryo");

    private final byte code;
    private final String desc;

    public static SerializerType from(byte code) {
        return Arrays.stream(values())
                .filter(o -> o.getCode() == code)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("code异常" + code));
    }
}
