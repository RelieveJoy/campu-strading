package com.campus.controller;

import com.campus.annotation.ApiLog;
import com.campus.entity.Announcement;
import com.campus.result.Result;
import com.campus.service.AnnouncementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "公告栏相关接口")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @ApiLog("公告")
    @Operation(summary = "获取最新公告列表")
    @GetMapping
    public Result<List<Announcement>> list(@RequestParam(defaultValue = "5") int limit) {
        log.info("查询公告列表, limit={}", limit);
        List<Announcement> list = announcementService.list(limit);
        return Result.success(list);
    }
}
