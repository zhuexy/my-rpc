package com.zxy.rpc.breaker;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 熔断器
 *
 * @author zxy
 * @date 2026/1/15 18:53
 **/
@Slf4j
public class CircuitBreaker {
    private State state = State.CLOSED;
    private final AtomicInteger successCount = new AtomicInteger(0);
    private final AtomicInteger failCount = new AtomicInteger(0);
    private final AtomicInteger totalCount = new AtomicInteger(0);

    // 失败阈值
    private final double failRateThreshold;
    // 最小请求数，熔断器开始计算失败率或成功率前需要的最小请求数量
    private final int minRequestCount;
    // 成功阈值 (半开状态下的)
    private final double successRateThreshold;
    // 熔断时间窗口（熔断多久）
    private final long windowTime;
    // 上次失败时间戳
    private long lastFailTime = 0L;

    public CircuitBreaker(double failRateThreshold, int minRequestCount, double successRateThreshold, long windowTime) {
        this.failRateThreshold = failRateThreshold;
        this.minRequestCount = minRequestCount;
        this.successRateThreshold = successRateThreshold;
        this.windowTime = windowTime;
    }

    public synchronized boolean allowRequest() {
        switch (state) {
            case CLOSED:
                return true;
            case OPEN:
                // 检查是否超过熔断时间窗口
                if (System.currentTimeMillis() - lastFailTime <= windowTime) {
                    return false;
                }
                state = State.HALF_OPEN;
                resetCount();
                return true;
            case HALF_OPEN:
                totalCount.incrementAndGet();
                return true;
            default:
                throw new IllegalArgumentException("Unknown state: " + state);
        }
    }

    public synchronized void success() {
        if (state != State.HALF_OPEN) {
            resetCount();
            return;
        }
        successCount.incrementAndGet();
        if (totalCount.get() >= minRequestCount && successCount.get() >= totalCount.get() * successRateThreshold) {
            log.info("熔断器从半开状态恢复到闭合状态");
            state = State.CLOSED;
            resetCount();
        }
    }

    public synchronized void fail() {
        failCount.incrementAndGet();
        totalCount.incrementAndGet();
        lastFailTime = System.currentTimeMillis();
        if (state == State.HALF_OPEN ||
                (totalCount.get() >= minRequestCount && failCount.get() >= failRateThreshold * totalCount.get())) {
            state = State.OPEN;
        }
    }

    private void resetCount() {
        successCount.set(0);
        failCount.set(0);
        totalCount.set(0);
    }

    enum State {
        CLOSED,
        OPEN,
        HALF_OPEN
    }
}
