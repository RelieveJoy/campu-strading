package com.campus.controller;

import com.campus.result.Result;
import com.campus.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "搜索相关接口")
public class SearchController {

    private final SearchService searchService;

    @Operation(summary = "搜索建议")
    @GetMapping("/suggestions")
    public Result<List<String>> suggestions(@RequestParam String q) {
        log.info("搜索建议: q={}", q);
        List<String> list = searchService.suggestions(q);
        return Result.success(list);
    }

    @Operation(summary = "清除搜索历史")
    @DeleteMapping("/history")
    public Result<String> clearHistory() {
        return Result.success();
    }
}
