package com.zxy.rpc.registry.zk;

import cn.hutool.core.util.StrUtil;
import com.zxy.rpc.constant.RpcConst;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.List;

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
        if (client.checkExists().forPath(path) != null) {
            log.info("节点已存在: {}", path);
            return;
        }
        log.info("创建节点: {}", path);
        client.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.PERSISTENT)
                .forPath(path);
    }

    @SneakyThrows
    public List<String> getChildren(String path) {
        if (StrUtil.isEmpty(path)) {
            throw new IllegalArgumentException("path can not be empty");
        }
        return client.getChildren().forPath(path);
    }
}
