package com.campus.controller;

import com.campus.result.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 举报 — 前端接口已暴露，后端待完善
 */
@RestController
@RequestMapping("/api/reports")
@Slf4j
@Tag(name = "举报相关接口")
public class ReportController {

    @PostMapping
    public Result<?> submit(@RequestBody Object data) {
        log.info("收到举报：{}", data);
        return Result.success();
    }
}
