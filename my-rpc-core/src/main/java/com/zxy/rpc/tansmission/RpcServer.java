package com.zxy.rpc.tansmission;

import cn.hutool.core.util.ClassUtil;
import com.zxy.rpc.annotation.RpcService;
import com.zxy.rpc.annotation.RpcServiceScan;
import com.zxy.rpc.config.RpcServiceConfig;

/**
 * @author zxy
 * @date 2025/12/27 16:46
 **/
public interface RpcServer {

    void start();

    void publishService(RpcServiceConfig config);

    default void scanService(Class<?> mainClass) {
        RpcServiceScan rpcServiceScan = mainClass.getAnnotation(RpcServiceScan.class);
        if (rpcServiceScan == null) {
            return;
        }
        String[] basePackages = rpcServiceScan.value();
        for (String basePackage : basePackages) {
            ClassUtil.scanPackageByAnnotation(basePackage, RpcService.class)
                    .forEach(clazz -> {
                        RpcService rpcService = clazz.getAnnotation(RpcService.class);
                        RpcServiceConfig config = new RpcServiceConfig();
                        config.setGroup(rpcService.group());
                        config.setVersion(rpcService.version());
                        try {
                            config.setService(clazz.newInstance());
                        } catch (Exception e) {
                            throw new RuntimeException("创建服务实例失败", e);
                        }
                        publishService(config);
                    });
        }
    }

}
