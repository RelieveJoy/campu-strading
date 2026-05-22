package com.campus.controller;

import com.campus.result.Result;
import com.campus.service.StatsService;
import com.campus.vo.OrderStatisticsVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stats")
@Slf4j
@Api(tags = "数据统计接口")
public class StatsController {

    @Autowired
    private StatsService statsService;

    @ApiOperation("各分类商品数量")
    @GetMapping("/category")
    public Result<List<OrderStatisticsVO>> countByCategory() {
        log.info("统计各分类商品数量");
        List<OrderStatisticsVO> list = statsService.countByCategory();
        return Result.success(list);
    }

    @ApiOperation("价格区间分布")
    @GetMapping("/price-range")
    public Result<List<OrderStatisticsVO>> countByPriceRange() {
        log.info("统计价格区间分布");
        List<OrderStatisticsVO> list = statsService.countByPriceRange();
        return Result.success(list);
    }

    @ApiOperation("近30天交易趋势")
    @GetMapping("/trade-trend")
    public Result<List<OrderStatisticsVO>> countTradeByDay() {
        log.info("统计近30天交易趋势");
        List<OrderStatisticsVO> list = statsService.countTradeByDay();
        return Result.success(list);
    }
}
