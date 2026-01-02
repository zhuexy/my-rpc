package com.zxy.rpc.tansmission.socket;

import com.zxy.rpc.config.RpcServiceConfig;
import com.zxy.rpc.constant.RpcConst;
import com.zxy.rpc.factory.SingletonFactory;
import com.zxy.rpc.handler.RpcReqHandler;
import com.zxy.rpc.handler.SocketRpcServerHandler;
import com.zxy.rpc.provider.ServiceProvider;
import com.zxy.rpc.provider.impl.ZkServiceProvider;
import com.zxy.rpc.tansmission.RpcServer;
import com.zxy.rpc.util.ShutdownHookUtils;
import com.zxy.rpc.util.ThreadPoolUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

/**
 * @author zxy
 * @date 2025/12/27 17:20
 **/
@Slf4j
@AllArgsConstructor
public class SocketRpcServer implements RpcServer {

    private Integer port;

    private final ServiceProvider serviceProvider;

    private final RpcReqHandler rpcReqHandler;

    private final ExecutorService executor;

    private static final String DEFAULT_POOL_NAME = "socket-rpc-server-";

    public SocketRpcServer() {
        this(RpcConst.SERVER_PORT);
    }

    public SocketRpcServer(Integer port) {
        this(port, SingletonFactory.getInstance(ZkServiceProvider.class));
    }

    public SocketRpcServer(Integer port, ServiceProvider serviceProvider) {
        this.port = port;
        this.serviceProvider = serviceProvider;
        this.rpcReqHandler = new RpcReqHandler(serviceProvider);
        this.executor = ThreadPoolUtil.crateIoIntensiveThreadPool(DEFAULT_POOL_NAME);
    }

    @Override
    public void start() {
        // 添加关闭钩子
        ShutdownHookUtils.clearAll();
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            log.info("SocketRpcServer start: {}", port);

            Socket socket;
            while ((socket = serverSocket.accept()) != null) {
                executor.submit(new SocketRpcServerHandler(socket, rpcReqHandler));
            }
        } catch (Exception e) {
            log.error("SocketRpcServer start error: ", e);
        }
    }

    @Override
    public void publishService(RpcServiceConfig config) {
        serviceProvider.publishService(config);
    }

}
