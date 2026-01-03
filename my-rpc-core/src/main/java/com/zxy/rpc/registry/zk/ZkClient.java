package com.zxy.rpc.registry.zk;

import cn.hutool.core.util.StrUtil;
import com.zxy.rpc.constant.RpcConst;
import com.zxy.rpc.util.IPUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zxy
 * @date 2026/1/1 16:23
 **/
@Slf4j
public class ZkClient {
    private final CuratorFramework client;
    // 重试之间等待的初试时间
    private static final int BASE_SLEEP_TIME = 1000;
    // 最大重试次数
    private static final int MAX_RETRIES = 3;
    // 服务地址缓存(key: /my-rpc/xxxService, value: List<String> ip:port)
    private static final Map<String, List<String>> SERVICE_ADDRESS_CACHE = new ConcurrentHashMap<>();
    // 缓存服务地址(/my-rpc/xxxService/ip:port)
    private static final Set<String> SERVICE_ADDRESS_SET = ConcurrentHashMap.newKeySet();

    public ZkClient(String hostname, int port) {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);
        this.client = CuratorFrameworkFactory.builder()
                .connectString(hostname + StrUtil.COLON + port)
                .retryPolicy(retryPolicy)
                .build();
        log.info("zkClient start: {}", hostname + StrUtil.COLON + port);
        this.client.start();
        log.info("zkClient start success");
    }

    public ZkClient() {
        this(RpcConst.ZK_IP, RpcConst.ZK_PORT);
    }

    @SneakyThrows
    public void createPersistentNode(String path) {
        if (StrUtil.isEmpty(path)) {
            throw new IllegalArgumentException("path can not be empty");
        }
        if (SERVICE_ADDRESS_SET.contains(path)) {
            log.info("节点已存在: {}", path);
            return;
        }
        if (client.checkExists().forPath(path) != null) {
            log.info("节点已存在: {}", path);
            SERVICE_ADDRESS_SET.add(path);
            return;
        }
        log.info("创建节点: {}", path);
        client.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.PERSISTENT)
                .forPath(path);
        SERVICE_ADDRESS_SET.add(path);
    }

    @SneakyThrows
    public List<String> getChildren(String path) {
        if (StrUtil.isEmpty(path)) {
            throw new IllegalArgumentException("path can not be empty");
        }
        if (SERVICE_ADDRESS_CACHE.containsKey(path)) {
            return SERVICE_ADDRESS_CACHE.get(path);
        }
        List<String> children = client.getChildren().forPath(path);
        SERVICE_ADDRESS_CACHE.put(path, children);

        // 监听节点
        watchNode(path);

        return children;
    }

    public void clearAll(InetSocketAddress inetSocketAddress) {
        if (inetSocketAddress == null) {
            throw new IllegalArgumentException("address can not be null");
        }
        String address = IPUtils.toStringAddress(inetSocketAddress);
        SERVICE_ADDRESS_SET.forEach(path -> {
            // 如果路径以节点地址结尾，则删除该节点
            if (path.endsWith(address)) {
                try {
                    client.delete().forPath(path);
                    log.debug("删除节点: {}", path);
                } catch (Exception e) {
                    log.error("删除节点失败: {}", path, e);
                }
            }
        });
    }

    // 监听节点
    @SneakyThrows
    private void watchNode(String path) {
        if (StrUtil.isEmpty(path)) {
            throw new IllegalArgumentException("path can not be empty");
        }
        // 用于监听指定路径的子节点变化
        PathChildrenCache pathChildrenCache = new PathChildrenCache(client, path, true);
        // 添加监听器，当节点变化时，更新缓存
        PathChildrenCacheListener listener = (curClient, event) -> {
            List<String> children = client.getChildren().forPath(path);
            SERVICE_ADDRESS_CACHE.put(path, children);
        };
        pathChildrenCache.getListenable().addListener(listener);
        pathChildrenCache.start();
    }
}
