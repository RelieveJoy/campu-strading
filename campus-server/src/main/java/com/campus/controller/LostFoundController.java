package com.campus.controller;

import com.campus.dto.LostFoundDTO;
import com.campus.dto.LostFoundPageQueryDTO;
import com.campus.result.PageResult;
import com.campus.result.Result;
import com.campus.service.LostFoundService;
import com.campus.vo.LostFoundDetailVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lostfound")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "失物招领相关接口")
public class LostFoundController {

    private final LostFoundService lostFoundService;

    @Operation(summary = "分页查询失物招领")
    @GetMapping
    public Result<PageResult> list(@RequestParam(required = false) String keyword,
                                    @RequestParam(required = false) String category,
                                    @RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "12") int pageSize) {
        LostFoundPageQueryDTO dto = new LostFoundPageQueryDTO();
        dto.setKeyword(keyword);
        dto.setCategory(category);
        dto.setPage(page);
        dto.setPageSize(pageSize);
        log.info("分页查询失物招领: keyword={}, category={}", keyword, category);
        PageResult result = lostFoundService.pageQuery(dto);
        return Result.success(result);
    }

    @Operation(summary = "失物招领详情")
    @GetMapping("/{id}")
    public Result<LostFoundDetailVO> detail(@PathVariable Long id) {
        log.info("查询失物招领详情 id={}", id);
        LostFoundDetailVO detail = lostFoundService.getById(id);
        return Result.success(detail);
    }

    @Operation(summary = "发布失物招领")
    @PostMapping
    public Result<String> publish(@RequestBody @Valid LostFoundDTO dto) {
        log.info("发布失物招领: {}", dto);
        lostFoundService.publish(dto);
        return Result.success();
    }

}
