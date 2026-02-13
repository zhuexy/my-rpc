package com.zxy;

import com.zxy.rpc.annotation.RpcServiceScan;
import com.zxy.rpc.constant.RpcConst;
import com.zxy.rpc.tansmission.RpcServer;
import com.zxy.rpc.tansmission.netty.NettyRpcServer;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zxy
 * @date 2025/12/27 16:51
 **/
@Slf4j
@RpcServiceScan("com.zxy.service.impl")
public class Main {
    public static void main(String[] args) {
        RpcServer server = new NettyRpcServer(RpcConst.SERVER_PORT);
        log.info("start server...");
        server.scanService(Main.class);
        server.start();
    }
}
