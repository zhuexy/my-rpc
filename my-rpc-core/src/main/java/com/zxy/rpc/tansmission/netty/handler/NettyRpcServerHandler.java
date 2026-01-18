package com.zxy.rpc.tansmission.netty.handler;

import com.zxy.rpc.dto.RpcMsg;
import com.zxy.rpc.dto.RpcReq;
import com.zxy.rpc.dto.RpcResp;
import com.zxy.rpc.enums.MsgType;
import com.zxy.rpc.handler.RpcReqHandler;
import com.zxy.rpc.provider.ServiceProvider;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
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
        // 处理请求并生成响应
        RpcMsg respRpcMsg = RpcMsg.builder()
                .requestId(rpcMsg.getRequestId())
                .version(rpcMsg.getVersion())
                .serializeType(rpcMsg.getSerializeType())
                .compressType(rpcMsg.getCompressType())
                .build();
        if (rpcMsg.getMsgType().isHeartbeat()) {
            respRpcMsg.setMsgType(MsgType.HEARTBEAT_RESP);
            log.info("Received heartbeat from client: {}", rpcMsg);
        } else {
            respRpcMsg.setMsgType(MsgType.RPC_RESP);
            RpcReq rpcReq = (RpcReq) rpcMsg.getData();
            log.info("Received RPC request: {}", rpcReq);
            RpcResp<?> rpcResp = handleRpcReq(rpcReq);
            respRpcMsg.setData(rpcResp);
        }
        ctx.channel().writeAndFlush(respRpcMsg).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("Exception caught in NettyRpcServerHandler", cause);
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        boolean isNeedClose = evt instanceof IdleStateEvent
                && ((IdleStateEvent) evt).state() == IdleState.READER_IDLE;
        if (!isNeedClose) {
            super.userEventTriggered(ctx, evt);
            return;
        }
        log.info("heartbeat timeout, closing channel: {}", ctx.channel().remoteAddress());
        ctx.channel().close();
    }

    private RpcResp<?> handleRpcReq(RpcReq rpcReq) {
        try {
            Object result = rpcReqHandler.handle(rpcReq);
            return RpcResp.success(rpcReq.getRequestId(), result);
        } catch (Exception e) {
            log.error("远程调用出现异常", e);
            return RpcResp.fail(rpcReq.getRequestId(), e.getMessage());
        }
    }
}
