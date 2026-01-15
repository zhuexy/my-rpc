package com.zxy.rpc.tansmission.netty;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * @author zxy
 * @date 2026/1/14 14:50
 **/
@Slf4j
public class ChannelPool {
    private static final Map<String, Channel> CHANNEL_MAP = new ConcurrentHashMap<>();

    public static Channel get(InetSocketAddress addr, Supplier<Channel> supplier) {
        String addrStr = addr.toString();
        Channel channel = CHANNEL_MAP.get(addrStr);
        if (channel != null && channel.isActive()) {
            log.info("channel exists {}", addrStr);
            return channel;
        }
        Channel newChannel = supplier.get();
        CHANNEL_MAP.put(addrStr, newChannel);
        return newChannel;
    }
}
