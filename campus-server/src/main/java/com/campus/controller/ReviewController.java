package com.campus.controller;

import com.campus.annotation.ApiLog;
import com.campus.dto.ReviewDTO;
import com.campus.result.PageResult;
import com.campus.result.Result;
import com.campus.service.ReviewService;
import com.campus.vo.ItemRatingVO;
import com.campus.vo.ReviewVO;
import com.campus.vo.UserRatingVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@Slf4j
@Tag(name = "评价相关接口")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @ApiLog("提交评价")
    @Operation(summary = "提交评价")
    @PostMapping
    public Result<String> submit(@RequestBody @Valid ReviewDTO reviewDTO) {
        log.info("提交评价：{}", reviewDTO);
        reviewService.submit(reviewDTO);
        return Result.success();
    }

    @ApiLog("修改评价")
    @Operation(summary = "修改评价")
    @PutMapping("/{reviewId}")
    public Result<String> update(@PathVariable Long reviewId, @RequestBody @Valid ReviewDTO reviewDTO) {
        log.info("修改评价 id={}：{}", reviewId, reviewDTO);
        reviewService.update(reviewId, reviewDTO);
        return Result.success();
    }

    @ApiLog("查评价")
    @Operation(summary = "商品评价列表")
    @GetMapping("/item/{itemId}")
    public Result<List<ReviewVO>> listByItem(@PathVariable Long itemId) {
        log.info("查询商品 {} 的评价", itemId);
        List<ReviewVO> list = reviewService.getByItem(itemId);
        return Result.success(list);
    }

    @ApiLog("商品评分")
    @Operation(summary = "商品评分摘要")
    @GetMapping("/item/{itemId}/rating")
    public Result<ItemRatingVO> itemRating(@PathVariable Long itemId) {
        log.info("查询商品 {} 评分摘要", itemId);
        ItemRatingVO rating = reviewService.getItemRating(itemId);
        return Result.success(rating);
    }

    @ApiLog("用户评分")
    @Operation(summary = "用户综合评分")
    @GetMapping("/users/{userId}")
    public Result<UserRatingVO> userRating(@PathVariable Long userId) {
        log.info("查询用户 {} 综合评分", userId);
        UserRatingVO rating = reviewService.getUserRating(userId);
        return Result.success(rating);
    }

    @ApiLog("用户评价列表")
    @Operation(summary = "用户评价列表（他人对该用户所有商品的评价）")
    @GetMapping("/users/{userId}/list")
    public Result<PageResult> listByTargetUser(@PathVariable Long userId,
                                                @RequestParam(defaultValue = "1") int page,
                                                @RequestParam(defaultValue = "10") int pageSize) {
        log.info("查询用户 {} 评价列表, page={}, pageSize={}", userId, page, pageSize);
        PageResult result = reviewService.getByTargetUser(userId, page, pageSize);
        return Result.success(result);
    }
}
