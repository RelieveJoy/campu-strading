package com.campus.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记需要使缓存失效的方法。
 * 方法执行成功后，将删除 value 中指定的 Redis key 前缀对应的所有 key。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheInvalidate {

    /** 需要删除的缓存 key 前缀，默认为首页缓存 */
    String[] value() default {"cache:home:items"};
}
