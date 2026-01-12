package com.zxy.rpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;

/**
 * @author zxy
 * @date 2026/1/11 15:26
 **/
@Getter
@AllArgsConstructor
@ToString
public enum VersionType {
    VERSION1((byte) 1, "version1"),
    ;
    private final byte code;
    private final String desc;

    public static VersionType from(byte code) {
        return Arrays.stream(values())
                .filter(o -> o.getCode() == code)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("code异常" + code));
    }
}
