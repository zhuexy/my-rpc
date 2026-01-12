package com.zxy.rpc.serialize;

/**
 * @author zxy
 * @date 2026/1/11 15:46
 **/
public interface Serializer {
    byte[] serialize(Object obj);

    <T> T deserialize(byte[] data, Class<T> clazz);
}
