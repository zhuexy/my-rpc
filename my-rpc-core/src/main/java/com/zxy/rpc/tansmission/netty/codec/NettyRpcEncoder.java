package com.zxy.rpc.tansmission.netty.codec;

import com.zxy.rpc.compress.Compress;
import com.zxy.rpc.compress.impl.GzipCompress;
import com.zxy.rpc.constant.RpcConst;
import com.zxy.rpc.dto.RpcMsg;
import com.zxy.rpc.factory.SingletonFactory;
import com.zxy.rpc.serialize.Serializer;
import com.zxy.rpc.serialize.impl.KryoSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zxy
 * @date 2026/1/11 15:16
 **/
public class NettyRpcEncoder extends MessageToByteEncoder<RpcMsg> {

    private static final AtomicInteger ID_GENERATOR = new AtomicInteger(0);

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcMsg rpcMsg, ByteBuf byteBuf) throws Exception {
        // 写入魔法数
        byteBuf.writeBytes(RpcConst.RPC_MAGIC_CODE);
        // 写入版本号
        byteBuf.writeByte(rpcMsg.getVersion().getCode());
        // 写指针移动4位，为报文总长度留出空间
        int currIdx = byteBuf.writerIndex();
        byteBuf.writerIndex(currIdx + 4);
        // 写入消息类型、序列化类型、压缩类型
        byteBuf.writeByte(rpcMsg.getMsgType().getCode());
        byteBuf.writeByte(rpcMsg.getSerializeType().getCode());
        byteBuf.writeByte(rpcMsg.getCompressType().getCode());
        // 生成请求id并写入
        byteBuf.writeInt(ID_GENERATOR.getAndIncrement());

        int fullLen = RpcConst.RPC_REQ_HEAD_LEN;
        if (!rpcMsg.getMsgType().isHeartbeat() && rpcMsg.getData() != null) {
            byte[] data = data2Bytes(rpcMsg);
            fullLen += data.length;
            byteBuf.writeBytes(data);
        }

        currIdx = byteBuf.writerIndex();
        // 将写指针移动到写总长度的位置，先移动到最开始然后加上魔法数和版本号的长度就是总长度开始的位置
        byteBuf.writerIndex(currIdx - fullLen + RpcConst.RPC_MAGIC_CODE.length + 1);
        byteBuf.writeInt(fullLen);
        byteBuf.writerIndex(currIdx);
    }

    private byte[] data2Bytes(RpcMsg rpcMsg) {
        // TODO 根据序列化类型，压缩类型将数据转换称byte数组

        // 暂时先用简单方法实现
        Serializer serializer = SingletonFactory.getInstance(KryoSerializer.class);
        byte[] bytes = serializer.serialize(rpcMsg.getData());
        Compress compress = SingletonFactory.getInstance(GzipCompress.class);
        return compress.compress(bytes);

    }
}
