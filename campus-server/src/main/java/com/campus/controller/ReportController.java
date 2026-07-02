package com.campus.controller;

import com.campus.annotation.ApiLog;
import com.campus.dto.ReportDTO;
import com.campus.result.PageResult;
import com.campus.result.Result;
import com.campus.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "举报相关接口")
public class ReportController {

    private final ReportService reportService;

    @ApiLog("举报")
    @Operation(summary = "提交举报")
    @PostMapping
    public Result<String> submit(@RequestBody @Valid ReportDTO dto) {
        log.info("收到举报: {}", dto);
        reportService.submit(dto);
        return Result.success();
    }

    @ApiLog("查举报")
    @Operation(summary = "分页查询举报列表（管理员）")
    @GetMapping
    public Result<PageResult> list(@RequestParam(required = false) Integer status,
                                    @RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10") int pageSize) {
        log.info("查询举报列表, status={}, page={}, pageSize={}", status, page, pageSize);
        PageResult result = reportService.pageQuery(status, page, pageSize);
        return Result.success(result);
    }

    @ApiLog("处理举报")
    @Operation(summary = "标记举报已处理（管理员）")
    @PutMapping("/{id}/resolve")
    public Result<String> markResolved(@PathVariable Long id) {
        log.info("标记举报 {} 已处理", id);
        reportService.markResolved(id);
        return Result.success();
    }
}
