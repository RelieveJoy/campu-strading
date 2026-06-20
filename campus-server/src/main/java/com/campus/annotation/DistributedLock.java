package com.campus.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁注解。标注在方法上，方法执行前获取锁，执行后释放锁。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {

    /** 锁的 key，支持 SpEL 表达式，默认取类名+方法名 */
    String key() default "";

    /** 等待获取锁的最大时间，默认 5 秒 */
    long waitTime() default 5;

    /** 持有锁的最大时间（防止死锁），默认 30 秒 */
    long leaseTime() default 30;

    /** 时间单位 */
    TimeUnit timeUnit() default TimeUnit.SECONDS;
}
