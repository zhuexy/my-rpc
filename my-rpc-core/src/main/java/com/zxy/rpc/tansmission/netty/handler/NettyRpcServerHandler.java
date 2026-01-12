package com.zxy.rpc.tansmission.netty.handler;

import com.zxy.rpc.dto.RpcMsg;
import com.zxy.rpc.dto.RpcReq;
import com.zxy.rpc.dto.RpcResp;
import com.zxy.rpc.enums.CompressType;
import com.zxy.rpc.enums.MsgType;
import com.zxy.rpc.enums.SerializerType;
import com.zxy.rpc.enums.VersionType;
import com.zxy.rpc.handler.RpcReqHandler;
import com.zxy.rpc.provider.ServiceProvider;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zxy
 * @date 2026/1/9 18:34
 **/
@Slf4j
public class NettyRpcServerHandler extends SimpleChannelInboundHandler<RpcMsg> {

    private final RpcReqHandler rpcReqHandler;

    public NettyRpcServerHandler(ServiceProvider serviceProvider) {
        this.rpcReqHandler = new RpcReqHandler(serviceProvider);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcMsg rpcMsg) throws Exception {
        RpcReq rpcReq = (RpcReq) rpcMsg.getData();
        log.info("Received RPC request: {}", rpcReq);
        // 处理请求并生成响应
        Object result = rpcReqHandler.invoke(rpcReq);
        RpcResp<?> rpcResp = RpcResp.success(rpcReq.getRequestId(), result);
        RpcMsg respRpcMsg = RpcMsg.builder()
                .version(VersionType.VERSION1)
                .msgType(MsgType.RPC_RESP)
                .serializeType(SerializerType.KRYO)
                .compressType(CompressType.GZIP)
                .data(rpcResp)
                .build();
        ctx.channel().writeAndFlush(respRpcMsg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("Exception caught in NettyRpcServerHandler", cause);
        ctx.close();
    }
}
