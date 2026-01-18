package com.zxy.rpc.util;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.setting.dialect.Props;
import com.zxy.rpc.config.RpcConfig;

import java.net.URL;

/**
 * @author zxy
 * @date 2026/1/18 12:51
 **/
public class RpcConfigUtils {

    private static final String FILE_NAME = "rpc-config.properties";

    private static RpcConfig RPC_CONFIG;

    private RpcConfigUtils() {
    }

    private static void loadRpcConfig() {
        URL url = ResourceUtil.getResource(FILE_NAME);
        if (url == null) {
            RPC_CONFIG = new RpcConfig();
            return;
        }
        Props props = new Props(FILE_NAME);
        if (props.isEmpty()) {
            RPC_CONFIG = new RpcConfig();
            return;
        }
        RPC_CONFIG = props.toBean(RpcConfig.class);
    }

    public static RpcConfig getRpcConfig() {
        if (RPC_CONFIG == null) {
            loadRpcConfig();
        }
        return RPC_CONFIG;
    }
}
