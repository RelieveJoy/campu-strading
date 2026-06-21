package com.campus.controller;

import com.campus.result.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 公告栏 — 前端接口已暴露，后端待完善（当前返回空列表，前端降级使用默认公告）
 */
@RestController
@RequestMapping("/api/announcements")
@Slf4j
@Tag(name = "公告栏相关接口")
public class AnnouncementController {

    @GetMapping
    public Result<List<Map<String, Object>>> list() {
        return Result.success(new ArrayList<>());
    }
}
