package com.campus.controller;

import com.campus.entity.Banner;
import com.campus.result.Result;
import com.campus.service.BannerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/banners")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "轮播图相关接口")
public class BannerController {

    private final BannerService bannerService;

    @Operation(summary = "获取启用的轮播图列表")
    @GetMapping
    public Result<List<Banner>> list() {
        log.info("查询轮播图列表");
        List<Banner> list = bannerService.list();
        return Result.success(list);
    }
}
