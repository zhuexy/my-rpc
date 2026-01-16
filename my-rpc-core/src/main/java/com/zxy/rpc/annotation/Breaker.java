package com.zxy.rpc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Breaker {
    /**
     * 失败率阈值，范围0-1，表示允许的最大失败比例，超过则变为OPEN状态
     */
    double failRateThreshold() default 0.5;

    /**
     * 成功率阈值，范围0-1，表示允许的最小成功比例，超过则变为CLOSED状态
     */
    double successRateThreshold() default 0.5;

    /**
     * 最小请求数，熔断器开始计算失败率和成功率前需要的最小请求数量
     */
    int minRequestCount() default 5;

    /**
     * 熔断时间窗口大小，单位为毫秒
     */
    long windowSize() default 10000L;
}
