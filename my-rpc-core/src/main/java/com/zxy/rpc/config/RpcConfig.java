package com.zxy.rpc.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zxy
 * @date 2026/1/18 12:49
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RpcConfig {
    // 序列化方式
    private String serializer = "kryo";
    // 负载均衡方式
    private String loadBalance = "round";
    // 压缩方式
    private String compress = "gzip";
}
