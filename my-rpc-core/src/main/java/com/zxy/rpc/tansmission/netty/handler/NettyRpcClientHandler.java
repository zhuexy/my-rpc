package com.zxy.rpc.tansmission.netty.handler;

import com.zxy.rpc.dto.RpcMsg;
import com.zxy.rpc.dto.RpcResp;
import com.zxy.rpc.enums.CompressType;
import com.zxy.rpc.enums.MsgType;
import com.zxy.rpc.enums.SerializerType;
import com.zxy.rpc.enums.VersionType;
import com.zxy.rpc.tansmission.netty.NettyRpcClient;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zxy
 * @date 2026/1/10 19:53
 **/
@Slf4j
public class NettyRpcClientHandler extends SimpleChannelInboundHandler<RpcMsg> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcMsg rpcMsg) throws Exception {
        if (rpcMsg.getMsgType().isHeartbeat()) {
            log.info("Received heartbeat from server: {}", rpcMsg);
            return;
        }
        // 处理接收到的响应
        RpcResp<?> rpcResp = (RpcResp<?>) rpcMsg.getData();
        log.info("Received RPC response: {}", rpcResp);
        // 将响应传递给等待的请求线程
        NettyRpcClient.UnprocessedRpcReq.complete(rpcResp);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 记录异常日志并关闭通道
        log.error("Exception caught in NettyRpcClientHandler", cause);
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        boolean isNeedHeartBeat = evt instanceof IdleStateEvent
                && ((IdleStateEvent) evt).state() == IdleState.WRITER_IDLE;
        if (!isNeedHeartBeat) {
            super.userEventTriggered(ctx, evt);
            return;
        }
        // 发送心跳消息
        RpcMsg rpcMsg = RpcMsg.builder()
                .version(VersionType.VERSION1)
                .serializeType(SerializerType.KRYO)
                .compressType(CompressType.GZIP)
                .msgType(MsgType.HEARTBEAT_REQ)
                .build();
        ctx.channel().writeAndFlush(rpcMsg).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
    }
}
