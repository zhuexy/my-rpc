package com.zxy.rpc.handler;

import com.zxy.rpc.dto.RpcReq;
import com.zxy.rpc.dto.RpcResp;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author zxy
 * @date 2025/12/28 16:35
 **/
@Slf4j
@AllArgsConstructor
public class SocketRpcServerHandler implements Runnable {
    private final Socket socket;
    private final RpcReqHandler rpcReqHandler;

    @SneakyThrows
    @Override
    public void run() {
        // 读取请求
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        RpcReq req = (RpcReq) in.readObject();

        log.info("SocketRpcServer receive: {}", req);
        log.info("invoke rpc service");

        // 调用服务
        Object result = rpcReqHandler.invoke(req);

        // 响应
        RpcResp<?> resp = RpcResp.success(req.getRequestId(), result);
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        out.writeObject(resp);
        out.flush();
        log.info("SocketRpcServer send: {}", resp);
    }
}
