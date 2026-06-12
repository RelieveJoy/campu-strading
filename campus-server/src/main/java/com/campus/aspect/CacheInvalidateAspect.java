package com.campus.aspect;

import com.campus.annotation.CacheInvalidate;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * 缓存失效切面：拦截 @CacheInvalidate 标注的方法，执行成功后删除对应缓存。
 */
@Aspect
@Component
@Slf4j
public class CacheInvalidateAspect {

    @Autowired
    private RedisTemplate redisTemplate;

    @AfterReturning("@annotation(com.campus.annotation.CacheInvalidate)")
    public void invalidateCache(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        CacheInvalidate annotation = method.getAnnotation(CacheInvalidate.class);

        for (String prefix : annotation.value()) {
            try {
                Set<String> keys = redisTemplate.keys(prefix + "*");
                if (keys != null && !keys.isEmpty()) {
                    redisTemplate.delete(keys);
                    log.info("缓存已失效: {} ({} 个key)", prefix, keys.size());
                }
            } catch (Exception e) {
                log.warn("缓存失效失败（Redis 不可用）: {}", e.getMessage());
            }
        }
    }
}
