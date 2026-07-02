package com.campus.aspect;

import com.campus.annotation.ApiLog;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Set;

@Aspect
@Component
@Slf4j
public class ApiLogAspect {

    /** 敏感接口不打印参数体 */
    private static final Set<String> SENSITIVE_PATHS = Set.of(
            "/api/users/login", "/api/users/register", "/api/users/password"
    );

    @Around("@annotation(apiLog)")
    public Object logApi(ProceedingJoinPoint joinPoint, ApiLog apiLog) throws Throwable {
        long start = System.currentTimeMillis();
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String method = "UNKNOWN";
        String url = "UNKNOWN";
        String ip = "UNKNOWN";

        if (attrs != null) {
            HttpServletRequest req = attrs.getRequest();
            method = req.getMethod();
            url = req.getRequestURI();
            ip = req.getHeader("X-Forwarded-For");
            if (ip == null || ip.isBlank()) ip = req.getRemoteAddr();
        }

        String desc = apiLog.value().isBlank() ? "" : "[" + apiLog.value() + "] ";
        boolean sensitive = SENSITIVE_PATHS.contains(url);

        try {
            Object result = joinPoint.proceed();
            long elapsed = System.currentTimeMillis() - start;
            if (sensitive) {
                log.info("{} {} {} {}from={} {}ms", method, url, desc, "***", ip, elapsed);
            } else {
                log.info("{} {} {}{} from={} {}ms", method, url, desc, joinPoint.getArgs(), ip, elapsed);
            }
            return result;
        } catch (Throwable e) {
            long elapsed = System.currentTimeMillis() - start;
            log.warn("{} {} {}FAIL from={} {}ms {}: {}", method, url, desc, ip, elapsed,
                    e.getClass().getSimpleName(), e.getMessage());
            throw e;
        }
    }
}
