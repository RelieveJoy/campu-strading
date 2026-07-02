package com.campus.controller;

import com.campus.annotation.ApiLog;
import com.campus.dto.FavoriteDTO;
import com.campus.result.Result;
import com.campus.service.FavoriteService;
import com.campus.vo.FavoriteVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@Slf4j
@Tag(name = "收藏相关接口")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @ApiLog("收藏")
    @Operation(summary = "收藏商品")
    @PostMapping
    public Result<String> add(@RequestBody @Valid FavoriteDTO favoriteDTO) {
        log.info("收藏商品：{}", favoriteDTO);
        favoriteService.add(favoriteDTO);
        return Result.success();
    }

    @ApiLog("取消收藏")
    @Operation(summary = "取消收藏")
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        log.info("取消收藏 id={}", id);
        favoriteService.delete(id);
        return Result.success();
    }

    @ApiLog("查收藏")
    @Operation(summary = "我的收藏列表")
    @GetMapping
    public Result<List<FavoriteVO>> list() {
        log.info("查询收藏列表");
        List<FavoriteVO> list = favoriteService.list();
        return Result.success(list);
    }

    @ApiLog("查收藏状态")
    @Operation(summary = "检查是否已收藏")
    @GetMapping("/check")
    public Result<Boolean> check(@RequestParam Long itemId) {
        log.info("检查是否已收藏 itemId={}", itemId);
        boolean result = favoriteService.check(itemId);
        return Result.success(result);
    }
}
