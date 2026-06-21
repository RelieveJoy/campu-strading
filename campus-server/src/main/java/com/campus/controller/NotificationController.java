package com.campus.controller;

import com.campus.context.BaseContext;
import com.campus.mapper.NotificationMapper;
import com.campus.result.Result;
import com.campus.vo.NotificationVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "通知相关接口")
public class NotificationController {

    private final NotificationMapper notificationMapper;

    @Operation(summary = "获取当前用户的通知列表")
    @GetMapping
    public Result<List<NotificationVO>> list() {
        List<NotificationVO> list = notificationMapper.listByUserId(BaseContext.getCurrentId());
        return Result.success(list);
    }

    @Operation(summary = "全部标记已读")
    @PostMapping("/read")
    public Result<String> markAllRead() {
        notificationMapper.markAllRead(BaseContext.getCurrentId());
        return Result.success();
    }
}
