package com.zxy.rpc.tansmission.netty;

import com.zxy.rpc.config.RpcServiceConfig;
import com.zxy.rpc.factory.SingletonFactory;
import com.zxy.rpc.provider.ServiceProvider;
import com.zxy.rpc.provider.impl.ZkServiceProvider;
import com.zxy.rpc.tansmission.RpcServer;
import com.zxy.rpc.tansmission.netty.codec.NettyRpcDecoder;
import com.zxy.rpc.tansmission.netty.codec.NettyRpcEncoder;
import com.zxy.rpc.tansmission.netty.handler.NettyRpcServerHandler;
import com.zxy.rpc.util.ShutdownHookUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 使用netty实现服务端
 *
 * @author zxy
 * @date 2026/1/3 16:40
 **/
@AllArgsConstructor
@Slf4j
public class NettyRpcServer implements RpcServer {

    private final ServiceProvider serviceProvider;

    private int port;

    public NettyRpcServer(int port) {
        this(SingletonFactory.getInstance(ZkServiceProvider.class), port);
    }

    @Override
    public void start() {
        ShutdownHookUtils.clearAll();
        ServerBootstrap serverBootstrap = new ServerBootstrap()
                .group(new NioEventLoopGroup(), new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        // 添加处理器
                        socketChannel.pipeline().addLast(new IdleStateHandler(
                                30, 0, 0, TimeUnit.SECONDS));
                        socketChannel.pipeline().addLast(new NettyRpcDecoder());
                        socketChannel.pipeline().addLast(new NettyRpcEncoder());
                        socketChannel.pipeline().addLast(new NettyRpcServerHandler(serviceProvider));
                    }
                });
        try {
            ChannelFuture future = serverBootstrap.bind(this.port).sync();
            log.info("Netty server started on port {}", this.port);
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("Netty server start error:", e);
        }
    }

    @Override
    public void publishService(RpcServiceConfig config) {
        serviceProvider.publishService(config);
    }
}
