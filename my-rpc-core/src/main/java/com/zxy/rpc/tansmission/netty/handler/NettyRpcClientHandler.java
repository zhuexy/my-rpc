package com.zxy.rpc.tansmission.netty.handler;

import com.alibaba.fastjson2.JSON;
import com.zxy.rpc.dto.RpcResp;
import com.zxy.rpc.tansmission.netty.NettyRpcClient;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zxy
 * @date 2026/1/10 19:53
 **/
@Slf4j
public class NettyRpcClientHandler extends SimpleChannelInboundHandler<String> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String rpcRespStr) throws Exception {
        // 处理接收到的响应
        RpcResp<?> rpcResp = JSON.parseObject(rpcRespStr, RpcResp.class);
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
}
