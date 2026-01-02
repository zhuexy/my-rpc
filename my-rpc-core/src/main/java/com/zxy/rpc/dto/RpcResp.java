package com.zxy.rpc.dto;


import com.zxy.rpc.enums.RpcRespStatus;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zxy
 * @date 2025/12/27 16:35
 **/
@Data
public class RpcResp<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private String requestId;
    private Integer code;
    private T data;
    private String msg;

    public static <T> RpcResp<T> success(String requestId, T data) {
        RpcResp<T> rpcResp = new RpcResp<>();
        rpcResp.setRequestId(requestId);
        rpcResp.setData(data);
        rpcResp.setCode(200);
        return rpcResp;
    }

    public static <T> RpcResp<T> fail(String requestId, RpcRespStatus status) {
        return fail(requestId, status.getMsg());
    }

    public static <T> RpcResp<T> fail(String requestId, String msg) {
        RpcResp<T> rpcResp = new RpcResp<>();
        rpcResp.setRequestId(requestId);
        rpcResp.setCode(RpcRespStatus.FAIL.getCode());
        rpcResp.setMsg(msg);
        return rpcResp;
    }
}
