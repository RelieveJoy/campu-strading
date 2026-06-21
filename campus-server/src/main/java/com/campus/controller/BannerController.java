package com.campus.controller;

import com.campus.result.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 首页轮播图 — 前端接口已暴露，后端待完善（当前返回空列表，前端降级不显示轮播）
 */
@RestController
@RequestMapping("/api/banners")
@Slf4j
@Tag(name = "轮播图相关接口")
public class BannerController {

    @GetMapping
    public Result<List<Map<String, Object>>> list() {
        return Result.success(new ArrayList<>());
    }
}
