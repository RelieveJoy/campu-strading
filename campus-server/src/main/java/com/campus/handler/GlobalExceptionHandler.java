package com.campus.handler;

import com.campus.constant.MessageConstant;
import com.campus.exception.BaseException;
import com.campus.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex) {
        log.error("业务异常：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /**
     * 捕获 SQL 完整性约束冲突（如唯一键重复）
     */
    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex) {
        log.error("数据完整性约束冲突", ex);
        return Result.error(MessageConstant.OPERATION_FAILED);
    }

    /**
     * 兜底：捕获所有未处理的异常，避免将异常堆栈暴露给前端
     */
    @ExceptionHandler
    public Result exceptionHandler(Exception ex) {
        log.error("系统异常", ex);
        return Result.error(MessageConstant.UNKNOWN_ERROR);
    }
}
