package com.zxy.rpc.serialize.impl;

import com.zxy.rpc.serialize.Serializer;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

/**
 * @author zxy
 * @date 2026/1/16 14:58
 **/
public class ProtostuffSerializer implements Serializer {
    private static final LinkedBuffer LINKED_BUFFER = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);

    @Override
    public byte[] serialize(Object obj) {
        Class<?> aClass = obj.getClass();
        Schema schema = RuntimeSchema.getSchema(aClass);
        try {
            return ProtostuffIOUtil.toByteArray(obj, schema, LINKED_BUFFER);
        } finally {
            LINKED_BUFFER.clear();
        }
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        Schema<T> schema = RuntimeSchema.getSchema(clazz);
        T t = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(data, t, schema);
        return t;
    }
}
