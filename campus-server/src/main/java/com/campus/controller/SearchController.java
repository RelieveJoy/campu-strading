package com.campus.controller;

import com.campus.result.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/**
 * 搜索建议/历史 — 前端接口已暴露，后端待完善（当前返回空列表）
 */
@RestController
@RequestMapping("/api/search")
@Slf4j
@Tag(name = "搜索相关接口")
public class SearchController {

    @GetMapping("/suggestions")
    public Result<?> suggestions(@RequestParam String q) {
        return Result.success(new ArrayList<>());
    }

    @DeleteMapping("/history")
    public Result<?> clearHistory() {
        return Result.success();
    }
}
