package com.zxy.rpc.util;

import cn.hutool.core.util.StrUtil;

import java.net.InetSocketAddress;

/**
 * @author zxy
 * @date 2026/1/2 0:09
 **/
public class IPUtils {

    // 将ip:port转换成InetSocketAddress
    public static InetSocketAddress toInetsocketAddress(String address) {
        if (StrUtil.isBlank(address)) {
            throw new IllegalArgumentException("address is null");
        }
        String[] split = address.split(StrUtil.COLON);
        if (split.length != 2) {
            throw new IllegalArgumentException("address format error");
        }
        String host = split[0];
        int port = Integer.parseInt(split[1]);
        return new InetSocketAddress(host, port);
    }

    // 将InetSocketAddress转换成ip:port
    public static String toStringAddress(InetSocketAddress address) {
        if (address == null) {
            throw new IllegalArgumentException("address is null");
        }
        return address.getAddress().getHostAddress() + StrUtil.COLON + address.getPort();
    }


}
