package com.zxy.rpc.tansmission.netty.codec;

import cn.hutool.core.util.ArrayUtil;
import com.zxy.rpc.compress.Compress;
import com.zxy.rpc.compress.impl.GzipCompress;
import com.zxy.rpc.constant.RpcConst;
import com.zxy.rpc.dto.RpcMsg;
import com.zxy.rpc.dto.RpcReq;
import com.zxy.rpc.dto.RpcResp;
import com.zxy.rpc.enums.CompressType;
import com.zxy.rpc.enums.MsgType;
import com.zxy.rpc.enums.SerializerType;
import com.zxy.rpc.enums.VersionType;
import com.zxy.rpc.exception.RpcException;
import com.zxy.rpc.factory.SingletonFactory;
import com.zxy.rpc.serialize.Serializer;
import com.zxy.rpc.serialize.impl.KryoSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zxy
 * @date 2026/1/11 16:03
 **/
@Slf4j
public class NettyRpcDecoder extends LengthFieldBasedFrameDecoder {
    public NettyRpcDecoder() {
        /*
         *  解决粘包问题
         *  @param maxFrameLength
         *        the maximum length of the frame.  If the length of the frame is
         *        greater than this value, {@link TooLongFrameException} will be
         *        thrown.
         * @param lengthFieldOffset
         *        the offset of the length field
         *        第几个字节开始是总长度字段
         * @param lengthFieldLength
         *        the length of the length field
         *        总长度字段占多少字节
         * @param lengthAdjustment
         *        the compensation value to add to the value of the length field
         *        对总长度字段读出来的值，进行修正，因为前面已经读过了一些字节
         * @param initialBytesToStrip
         *        the number of first bytes to strip out from the decoded frame
         *        从解码出的完整帧中，剥掉前 N 个字节
         */
        super(RpcConst.RPC_REQ_MAX_LEN, 5, 4, -9, 0);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf byteBuf = (ByteBuf) super.decode(ctx, in);

        return readRpcMsg(byteBuf);
    }

    private RpcMsg readRpcMsg(ByteBuf byteBuf) {
        byte[] magicBytes = new byte[RpcConst.RPC_MAGIC_CODE.length];
        byteBuf.readBytes(magicBytes);
        if (!ArrayUtil.equals(magicBytes, RpcConst.RPC_MAGIC_CODE)) {
            throw new RpcException("魔法数异常：" + new String(magicBytes));
        }
        byte versionCode = byteBuf.readByte();
        VersionType version = VersionType.from(versionCode);

        int fullLen = byteBuf.readInt();

        byte msgTypeCode = byteBuf.readByte();
        MsgType msgType = MsgType.from(msgTypeCode);

        byte serializerTypeCode = byteBuf.readByte();
        SerializerType serializerType = SerializerType.from(serializerTypeCode);

        byte compressTypeCode = byteBuf.readByte();
        CompressType compressType = CompressType.from(compressTypeCode);

        int reqId = byteBuf.readInt();

        Object data = null;
        if (!msgType.isHeartbeat()) {
            data = readData(byteBuf, msgType,
                    serializerType, compressType, fullLen - RpcConst.RPC_REQ_HEAD_LEN);
            log.info("data: {}", data);
        }
        return RpcMsg.builder()
                .version(version)
                .msgType(msgType)
                .serializeType(serializerType)
                .compressType(compressType)
                .requestId(reqId)
                .data(data)
                .build();
    }

    private Object readData(ByteBuf byteBuf, MsgType msgType,
                            SerializerType serializerType, CompressType compressType, int dataLen) {
        if (dataLen <= 0) {
            return null;
        }
        if (msgType.isReq()) {
            return readData(byteBuf, serializerType, compressType, dataLen, RpcReq.class);
        }
        return readData(byteBuf, serializerType, compressType, dataLen, RpcResp.class);
    }

    private <T> T readData(ByteBuf byteBuf, SerializerType serializerType,
                           CompressType compressType, int dataLen, Class<T> clazz) {
        byte[] dataBytes = new byte[dataLen];
        byteBuf.readBytes(dataBytes);

        // TODO 根据序列化类型、压缩类型来反序列化、解压

        Compress compress = SingletonFactory.getInstance(GzipCompress.class);
        dataBytes = compress.decompress(dataBytes);

        Serializer serializer = SingletonFactory.getInstance(KryoSerializer.class);
        return serializer.deserialize(dataBytes, clazz);
    }
}
