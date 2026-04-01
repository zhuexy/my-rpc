package com.zxy.api.serialize;

import com.zxy.rpc.serialize.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 *
 * @author zxy
 * @date 2026/3/31 22:55
 **/
public class JdkSerializer implements Serializer {
    @Override
    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            System.out.println("Using JDK serialization for object: " + obj);
            oos.writeObject(obj);
            oos.flush();
            return bos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("JDK serialization failed", e);
        }
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            Object obj = ois.readObject();
            System.out.println("Using JDK deserialization for data: " + obj);
            return clazz.cast(obj);
        } catch (Exception e) {
            throw new RuntimeException("JDK deserialization failed", e);
        }
    }
}
