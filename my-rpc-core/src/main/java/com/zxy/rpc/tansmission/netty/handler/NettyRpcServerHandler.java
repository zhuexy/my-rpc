package com.zxy.rpc.tansmission.netty.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.zxy.rpc.dto.RpcReq;
import com.zxy.rpc.dto.RpcResp;
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
public class NettyRpcServerHandler extends SimpleChannelInboundHandler<String> {

    private RpcReqHandler rpcReqHandler;

    public NettyRpcServerHandler(ServiceProvider serviceProvider) {
        this.rpcReqHandler = new RpcReqHandler(serviceProvider);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String rpcReqStr) throws Exception {
        RpcReq rpcReq = JSON.parseObject(rpcReqStr, RpcReq.class, JSONReader.Feature.SupportClassForName);
        log.info("Received RPC request: {}", rpcReq);
        // 处理请求并生成响应
        Object result = rpcReqHandler.invoke(rpcReq);
        RpcResp<?> rpcResp = RpcResp.success(rpcReq.getRequestId(), result);
        ctx.channel().writeAndFlush(JSON.toJSONString(rpcResp));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("Exception caught in NettyRpcServerHandler", cause);
        ctx.close();
    }
}
