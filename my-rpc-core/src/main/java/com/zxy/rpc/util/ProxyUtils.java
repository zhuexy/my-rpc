package com.zxy.rpc.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ClassUtil;
import com.zxy.rpc.annotation.RpcReference;
import com.zxy.rpc.annotation.RpcReferenceScan;
import com.zxy.rpc.config.RpcServiceConfig;
import com.zxy.rpc.factory.SingletonFactory;
import com.zxy.rpc.proxy.RpcClientProxy;
import com.zxy.rpc.tansmission.RpcClient;
import com.zxy.rpc.tansmission.netty.NettyRpcClient;

import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * @author zxy
 * @date 2025/12/29 1:27
 **/
public class ProxyUtils {

    private static final RpcClient RPC_CLIENT = SingletonFactory.getInstance(NettyRpcClient.class);

//    private static final RpcClientProxy RPC_CLIENT_PROXY = new RpcClientProxy(RPC_CLIENT);
//
//    public static <T> T getProxy(Class<T> clazz) {
//        return RPC_CLIENT_PROXY.getProxy(clazz);
//    }

    public static void scanReference(Class<?> mainClass) {
        RpcReferenceScan rpcReferenceScan = mainClass.getAnnotation(RpcReferenceScan.class);
        if (rpcReferenceScan == null) {
            return;
        }
        String[] basePackages = rpcReferenceScan.value();
        if (CollUtil.isEmpty(Arrays.asList(basePackages))) {
            return;
        }
        ClassUtil.scanPackage(basePackages[0]).forEach(clazz -> {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                RpcReference rpcReference = field.getAnnotation(RpcReference.class);
                if (rpcReference != null) {
                    try {
                        field.setAccessible(true);
                        Class<?> type = field.getType();
                        RpcServiceConfig config = RpcServiceConfig.builder()
                                .group(rpcReference.group())
                                .version(rpcReference.version())
                                .build();
                        RpcClientProxy rpcClientProxy = new RpcClientProxy(RPC_CLIENT, config);
                        Object proxy = rpcClientProxy.getProxy(type);
                        field.set(SingletonFactory.getInstance(clazz), proxy);
                    } catch (Exception e) {
                        throw new RuntimeException("创建服务实例失败", e);
                    }
                }
            }
        });
    }

}
