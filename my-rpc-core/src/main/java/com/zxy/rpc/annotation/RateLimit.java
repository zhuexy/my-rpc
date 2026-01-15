package com.zxy.rpc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    /**
     * 每秒允许的最大请求数
     */
    double permitsPerSecond();

    /**
     * 获取令牌的超时时间，单位毫秒
     */
    long timeout() default 0L;
}
