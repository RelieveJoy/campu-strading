package com.campus.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口限流注解。
 * 基于 IP + Redis ZSET 滑动窗口实现。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimiter {

    /** 限流标识，用于区分不同接口，如 "login" */
    String key();

    /** 时间窗口内最大请求数 */
    int limit();

    /** 时间窗口，单位秒 */
    int windowSeconds() default 60;
}
