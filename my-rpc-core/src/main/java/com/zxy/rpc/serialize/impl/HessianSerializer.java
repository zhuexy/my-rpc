package com.zxy.rpc.serialize.impl;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.zxy.rpc.serialize.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @author zxy
 * @date 2026/1/16 14:54
 **/
public class HessianSerializer implements Serializer {
    @Override
    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            HessianOutput hessianOutput = new HessianOutput(out);
            hessianOutput.writeObject(obj);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Hessian serialization failed", e);
        }
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        try (ByteArrayInputStream in = new ByteArrayInputStream(data)) {
            HessianInput hessianInput = new HessianInput(in);
            Object o = hessianInput.readObject();
            return clazz.cast(o);
        } catch (Exception e) {
            throw new RuntimeException("Hessian deserialization failed", e);
        }
    }
}
