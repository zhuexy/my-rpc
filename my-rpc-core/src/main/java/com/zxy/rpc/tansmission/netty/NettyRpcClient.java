package com.zxy.rpc.tansmission.netty;

import com.zxy.rpc.dto.RpcMsg;
import com.zxy.rpc.dto.RpcReq;
import com.zxy.rpc.dto.RpcResp;
import com.zxy.rpc.enums.CompressType;
import com.zxy.rpc.enums.MsgType;
import com.zxy.rpc.enums.SerializerType;
import com.zxy.rpc.enums.VersionType;
import com.zxy.rpc.exception.RpcException;
import com.zxy.rpc.factory.SingletonFactory;
import com.zxy.rpc.registry.ServiceDiscovery;
import com.zxy.rpc.registry.impl.ZkServiceDiscovery;
import com.zxy.rpc.tansmission.RpcClient;
import com.zxy.rpc.tansmission.netty.codec.NettyRpcDecoder;
import com.zxy.rpc.tansmission.netty.codec.NettyRpcEncoder;
import com.zxy.rpc.tansmission.netty.handler.NettyRpcClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


/**
 * 使用Netty实现客户端
 *
 * @author zxy
 * @date 2026/1/10 19:21
 **/
@Slf4j
public class NettyRpcClient implements RpcClient {

    private static final Bootstrap BOOTSTRAP;
    private static final Integer DEFAULT_CONNECT_TIMEOUT = 5000;
    private final ServiceDiscovery serviceDiscovery;

    public NettyRpcClient(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    public NettyRpcClient() {
        this.serviceDiscovery = SingletonFactory.getInstance(ZkServiceDiscovery.class);
    }

    static {
        BOOTSTRAP = new Bootstrap();
        BOOTSTRAP.group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, DEFAULT_CONNECT_TIMEOUT)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new IdleStateHandler(
                                0, 5, 0, TimeUnit.SECONDS));
                        ch.pipeline().addLast(new NettyRpcDecoder());
                        ch.pipeline().addLast(new NettyRpcEncoder());
                        ch.pipeline().addLast(new NettyRpcClientHandler());
                    }
                });
    }

    @SneakyThrows
    @Override
    public RpcResp<?> send(RpcReq rpcReq) {
        CompletableFuture<RpcResp<?>> cf = new CompletableFuture<>();
        UnprocessedRpcReq.put(rpcReq.getRequestId(), cf);
        // 1. 建立连接
        InetSocketAddress address = serviceDiscovery.lookupService(rpcReq);
        Channel channel = ChannelPool.get(address, () -> connect(address));
        // 2. 发送请求，等待响应
        RpcMsg rpcMsg = RpcMsg.builder()
                .version(VersionType.VERSION1)
                .serializeType(SerializerType.KRYO)
                .msgType(MsgType.RPC_REQ)
                .data(rpcReq)
                .compressType(CompressType.GZIP)
                .build();
        log.info("netty rpc client连接到: {}", address);
        channel.writeAndFlush(rpcMsg).addListener((ChannelFutureListener) future -> {
            if (!future.isSuccess()) {
                future.channel().close();
                cf.completeExceptionally(future.cause());
            }
        });
        // 3. 返回响应
        RpcResp<?> rpcResp = cf.get();
        log.debug("rpcResp: {}", rpcResp);
        return rpcResp;
    }

    private Channel connect(InetSocketAddress address) {
        try {
            return BOOTSTRAP.connect(address)
                    .sync()
                    .channel();
        } catch (InterruptedException e) {
            log.error("连接到远程服务器失败", e);
            throw new RuntimeException(e);
        }
    }

    public static class UnprocessedRpcReq {
        private static final Map<String, CompletableFuture<RpcResp<?>>> UNPROCESSED_REQ_MAP = new ConcurrentHashMap<>();

        public static void put(String requestId, CompletableFuture<RpcResp<?>> cf) {
            UNPROCESSED_REQ_MAP.put(requestId, cf);
        }

        public static void complete(RpcResp<?> rpcResp) {
            // 根据请求ID获取对应的CompletableFuture并完成它
            CompletableFuture<RpcResp<?>> cf = UNPROCESSED_REQ_MAP.remove(rpcResp.getRequestId());
            if (cf == null) {
                throw new RpcException("UnprocessedRpcReq请求异常");
            }
            cf.complete(rpcResp);
        }
    }
}
