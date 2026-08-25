package com.campus.exception;

/**
 * 接口限流异常 — 由 RateLimiterAspect 抛出，GlobalExceptionHandler 处理返回 429。
 */
public class RateLimitExceededException extends RuntimeException {

    public RateLimitExceededException(String message) {
        super(message);
    }
}
