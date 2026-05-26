package com.campus.aspect;

import com.campus.annotation.AutoFill;
import com.campus.constant.AutoFillConstant;
import com.campus.context.BaseContext;
import com.campus.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定义切面，实现自动填充功能
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    /**
     * 切入点
     */
    @Pointcut("execution(* com.campus.mapper.*.*(..)) && @annotation(com.campus.annotation.AutoFill)")
    public void autoFillPointCut() {

    }
    /**
     * 前置通知
     */
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) {
        log.info("开始进行公共字段的自动填充");
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        OperationType value = autoFill.value();

        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return;
        }

        Object arg = args[0];

        LocalDateTime now1 = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        if (value == OperationType.INSERT) {
            try {
                Method setCreateTime = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setCreateUser = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateTime = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                setCreateTime.invoke(arg, now1);
                setUpdateTime.invoke(arg, now1);
                setCreateUser.invoke(arg, currentId);
                setUpdateUser.invoke(arg, currentId);
            } catch (Exception e) {
                log.error("自动填充公共字段失败(INSERT): {}", e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }else if(value == OperationType.UPDATE){
            try {
                Method setUpdateTime = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                setUpdateTime.invoke(arg, now1);
                setUpdateUser.invoke(arg, currentId);
            } catch (Exception e) {
                log.error("自动填充公共字段失败(UPDATE): {}", e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }
    }
}
