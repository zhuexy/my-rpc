package com.zxy.rpc.tansmission;

import com.zxy.rpc.dto.RpcReq;
import com.zxy.rpc.dto.RpcResp;

/**
 * @author zxy
 * @date 2025/12/27 16:46
 **/
public interface RpcClient {

    RpcResp<?> send(RpcReq req);

}
