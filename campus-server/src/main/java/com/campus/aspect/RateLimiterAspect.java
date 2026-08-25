package com.campus.aspect;

import com.campus.annotation.RateLimiter;
import com.campus.exception.RateLimitExceededException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;
import java.util.Collections;

/**
 * 接口限流切面：基于 IP + Redis ZSET 滑动窗口。
 *
 * <p>全部操作封装在 Lua 脚本中原子执行，杜绝「查计数 → 写入」两步之间的并发穿透；
 * 每次写入刷新 key 的 TTL，避免闲置 key 永久驻留 Redis 内存。
 * 通过 {@link ObjectProvider} 注入：避免 {@code @ConditionalOnBean} 直接加在 @Component 上时，
 * 因自动配置类晚于组件扫描处理而导致的"切面从未被注册"问题；Redis 不可用时降级放行，不阻断业务。
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimiterAspect {

    private final ObjectProvider<StringRedisTemplate> stringRedisTemplateProvider;

    /**
     * 滑动窗口限流 Lua 脚本。
     *
     * KEYS[1] = 限流 key（rate_limit:{标识}:{IP}）
     * ARGV[1] = 窗口起点（毫秒时间戳 = now - windowSeconds*1000）
     * ARGV[2] = 本次请求时间戳（毫秒）
     * ARGV[3] = 窗口内最大请求数 limit
     * ARGV[4] = 窗口时长（秒），用于给 key 设置 TTL
     *
     * 返回 1 = 放行，0 = 拒绝。
     * Redis 保证单个 Lua 脚本执行期间其他命令不会穿插，因此整体是原子的。
     */
    private static final DefaultRedisScript<Long> RATE_LIMIT_SCRIPT = new DefaultRedisScript<>();

    static {
        RATE_LIMIT_SCRIPT.setScriptText(
                "redis.call('ZREMRANGEBYSCORE', KEYS[1], 0, ARGV[1])\n" +
                "local count = redis.call('ZCARD', KEYS[1])\n" +
                "if count >= tonumber(ARGV[3]) then\n" +
                "    return 0\n" +
                "end\n" +
                "redis.call('ZADD', KEYS[1], ARGV[2], ARGV[2])\n" +
                "redis.call('EXPIRE', KEYS[1], tonumber(ARGV[4]))\n" +
                "return 1"
        );
        RATE_LIMIT_SCRIPT.setResultType(Long.class);
    }

    @Around("@annotation(limiter)")
    public Object rateLimit(ProceedingJoinPoint joinPoint, RateLimiter limiter) throws Throwable {
        StringRedisTemplate stringRedisTemplate = stringRedisTemplateProvider.getIfAvailable();
        if (stringRedisTemplate == null) {
            log.warn("Redis 未配置，限流降级放行");
            return joinPoint.proceed();
        }

        String ip = getClientIp();
        String key = "rate_limit:" + limiter.key() + ":" + ip;
        long now = Instant.now().toEpochMilli();
        long windowStart = now - limiter.windowSeconds() * 1000L;

        try {
            // 一个 Lua 脚本原子完成：清旧记录 → 计数 → 判断 → 写入 → 设 TTL
            Long allowed = stringRedisTemplate.execute(
                    RATE_LIMIT_SCRIPT,
                    Collections.singletonList(key),
                    String.valueOf(windowStart),
                    String.valueOf(now),
                    String.valueOf(limiter.limit()),
                    String.valueOf(limiter.windowSeconds())
            );

            if (allowed == null || allowed != 1L) {
                log.warn("限流触发: key={}, ip={}, limit={}/{}s", limiter.key(), ip, limiter.limit(), limiter.windowSeconds());
                throw new RateLimitExceededException(
                        "请求过于频繁，请" + limiter.windowSeconds() + "秒后再试");
            }
        } catch (RateLimitExceededException e) {
            throw e;   // 真正的限流拒绝，原样上抛给 GlobalExceptionHandler
        } catch (Exception e) {
            // Redis 故障或脚本异常 → 降级放行，不让限流故障拖垮业务
            log.warn("限流执行异常，降级放行: {}", e.getMessage());
        }

        return joinPoint.proceed();
    }

    private String getClientIp() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) return "unknown";
        HttpServletRequest req = attrs.getRequest();
        String ip = req.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) ip = req.getRemoteAddr();
        return ip;
    }
}
