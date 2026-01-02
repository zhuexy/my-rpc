package com.zxy.rpc.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zxy
 * @date 2025/12/27 17:48
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RpcServiceConfig {
    // 服务版本
    private String version = "";
    // 服务组
    private String group = "";
    // 服务对象
    private Object service;

    public RpcServiceConfig(Object service) {
        this.service = service;
    }

    // 获取完整的服务名称(接口名称 + 版本 + 组)，如：com.zxy.rpc.service.UserServiceImpl + 版本 + 组
    public List<String> rpcServiceNames() {
        return getInterfaces().stream()
                .map(interfaceName -> interfaceName + getVersion() + getGroup())
                .collect(Collectors.toList());
    }

    // 获取服务对象实现的所有接口名称
    private List<String> getInterfaces() {
        return Arrays.stream(service.getClass().getInterfaces())
                .map(Class::getCanonicalName)
                .collect(Collectors.toList());
    }
}
