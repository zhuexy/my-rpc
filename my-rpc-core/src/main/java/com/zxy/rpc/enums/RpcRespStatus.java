package com.zxy.rpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @Author zxy
 * @Date 2025/12/27 16:39
 **/
@Getter
@ToString
@AllArgsConstructor
public enum RpcRespStatus {
    SUCCESS(200, "success"),
    FAIL(500, "fail"),
    ;

    private final Integer code;
    private final String msg;

}
