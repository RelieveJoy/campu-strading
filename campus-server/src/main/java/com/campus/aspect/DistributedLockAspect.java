package com.campus.aspect;

import com.campus.annotation.DistributedLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁切面：拦截 @DistributedLock 注解，用 Redisson 加锁执行。
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class DistributedLockAspect {

    private final RedissonClient redissonClient;

    @Around("@annotation(com.campus.annotation.DistributedLock)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DistributedLock lockAnno = method.getAnnotation(DistributedLock.class);

        // 解析 key：未指定则用"类名.方法名"
        String lockKey = lockAnno.key();
        if (lockKey.isEmpty()) {
            lockKey = signature.getDeclaringTypeName() + "." + method.getName();
        }

        RLock lock = redissonClient.getLock("lock:" + lockKey);
        boolean acquired = false;
        try {
            acquired = lock.tryLock(lockAnno.waitTime(), lockAnno.leaseTime(), lockAnno.timeUnit());
            if (!acquired) {
                log.warn("分布式锁获取失败: {}", lockKey);
                throw new RuntimeException("系统繁忙，请稍后重试");
            }
            log.debug("分布式锁已获取: {}", lockKey);
            return joinPoint.proceed();
        } finally {
            if (acquired) {
                lock.unlock();
                log.debug("分布式锁已释放: {}", lockKey);
            }
        }
    }
}
