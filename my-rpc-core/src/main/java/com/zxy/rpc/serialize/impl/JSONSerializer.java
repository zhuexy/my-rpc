package com.zxy.rpc.serialize.impl;

import com.alibaba.fastjson2.JSON;
import com.zxy.rpc.serialize.Serializer;

/**
 * @author zxy
 * @date 2026/1/13 1:46
 **/
public class JSONSerializer implements Serializer {
    @Override
    public byte[] serialize(Object obj) {
        return JSON.toJSONBytes(obj);
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        return JSON.parseObject(data, clazz);
    }
}
