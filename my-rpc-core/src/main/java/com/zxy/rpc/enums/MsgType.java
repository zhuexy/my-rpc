package com.zxy.rpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;

/**
 * @author zxy
 * @date 2026/1/11 15:28
 **/
@Getter
@AllArgsConstructor
@ToString
public enum MsgType {
    HEARTBEAT_REQ((byte) 0, "心跳请求"),
    HEARTBEAT_RESP((byte) 1, "心跳响应"),
    RPC_REQ((byte) 2, "RPC请求"),
    RPC_RESP((byte) 3, "RPC响应");

    private final byte code;
    private final String desc;

    public boolean isHeartbeat() {
        return this == HEARTBEAT_REQ || this == HEARTBEAT_RESP;
    }

    public boolean isReq() {
        return this == HEARTBEAT_REQ || this == RPC_REQ;
    }

    public static MsgType from(byte code) {
        return Arrays.stream(values())
                .filter(o -> o.getCode() == code)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("code异常"));
    }
}
