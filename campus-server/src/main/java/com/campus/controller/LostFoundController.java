package com.campus.controller;

import com.campus.result.PageResult;
import com.campus.result.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/**
 * 失物招领 — 前端接口已暴露，后端待完善（当前返回空列表）
 */
@RestController
@RequestMapping("/api/lostfound")
@Slf4j
@Tag(name = "失物招领相关接口")
public class LostFoundController {

    @GetMapping
    public Result<PageResult> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "12") Integer pageSize) {
        PageResult pageResult = new PageResult(0, new ArrayList<>());
        return Result.success(pageResult);
    }

    @GetMapping("/{id}")
    public Result<?> detail(@PathVariable Long id) {
        return Result.success(null);
    }

    @PostMapping
    public Result<?> publish(@RequestBody Object data) {
        return Result.success();
    }
}
