package com.zxy;

import com.zxy.rpc.config.RpcServiceConfig;
import com.zxy.rpc.constant.RpcConst;
import com.zxy.rpc.tansmission.RpcServer;
import com.zxy.rpc.tansmission.socket.SocketRpcServer;
import com.zxy.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zxy
 * @date 2025/12/27 16:51
 **/
@Slf4j
public class Main {
    public static void main(String[] args) {

        RpcServer server = new SocketRpcServer(RpcConst.SERVER_PORT);
        log.info("start server...");
        server.publishService(new RpcServiceConfig(new UserServiceImpl()));
        server.start();
    }
}
