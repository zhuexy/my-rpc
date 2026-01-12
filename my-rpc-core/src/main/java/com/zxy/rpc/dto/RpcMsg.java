package com.zxy.rpc.dto;

import com.zxy.rpc.enums.CompressType;
import com.zxy.rpc.enums.MsgType;
import com.zxy.rpc.enums.SerializerType;
import com.zxy.rpc.enums.VersionType;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 用于发送和接收的消息对象
 *
 * @author zxy
 * @date 2026/1/11 15:22
 **/
@Builder
@Data
public class RpcMsg implements Serializable {
    private static final long serialVersionUID = 1L;

    // 魔法数，用于标识协议包，4字节
    // 版本号，1字节
    private VersionType version;
    // 总长度，4字节
    // 消息类型，1字节
    private MsgType msgType;
    // 序列化类型，1字节
    private SerializerType serializeType;
    // 压缩类型，1字节
    private CompressType compressType;
    // 请求ID，4字节
    private Integer requestId;
    // 消息体
    private Object data;
}
