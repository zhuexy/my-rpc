package com.zxy.rpc.tansmission.netty;

import com.zxy.rpc.config.RpcServiceConfig;
import com.zxy.rpc.factory.SingletonFactory;
import com.zxy.rpc.provider.ServiceProvider;
import com.zxy.rpc.provider.impl.ZkServiceProvider;
import com.zxy.rpc.tansmission.RpcServer;
import com.zxy.rpc.tansmission.netty.handler.NettyRpcServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.AllArgsConstructor;

/**
 * 使用netty实现服务端
 *
 * @author zxy
 * @date 2026/1/3 16:40
 **/
@AllArgsConstructor
public class NettyRpcServer implements RpcServer {

    private final ServiceProvider serviceProvider;

    private int port;

    public NettyRpcServer(int port) {
        this(SingletonFactory.getInstance(ZkServiceProvider.class), port);
    }

    @Override
    public void start() {
        ServerBootstrap serverBootstrap = new ServerBootstrap()
                .group(new NioEventLoopGroup(), new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        // 添加处理器
                        socketChannel.pipeline().addLast(new StringDecoder());
                        socketChannel.pipeline().addLast(new StringEncoder());
                        socketChannel.pipeline().addLast(new NettyRpcServerHandler(serviceProvider));
                    }
                });
        serverBootstrap.bind(this.port);
    }

    @Override
    public void publishService(RpcServiceConfig config) {
        serviceProvider.publishService(config);
    }
}
