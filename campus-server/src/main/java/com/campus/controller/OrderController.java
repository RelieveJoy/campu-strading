package com.campus.controller;

import com.campus.annotation.ApiLog;
import com.campus.dto.OrdersDTO;
import com.campus.dto.OrdersPageQueryDTO;
import com.campus.result.PageResult;
import com.campus.result.Result;
import com.campus.service.OrderService;
import com.campus.vo.OrderVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@Slf4j
@Tag(name = "订单相关接口")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @ApiLog("下单")
    @Operation(summary = "创建订单")
    @PostMapping
    public Result<String> create(@RequestBody @Valid OrdersDTO ordersDTO) {
        log.info("创建订单：{}", ordersDTO);
        orderService.create(ordersDTO);
        return Result.success();
    }

    @ApiLog("确认订单")
    @Operation(summary = "确认完成订单")
    @PutMapping("/{id}/confirm")
    public Result<String> confirm(@PathVariable Long id) {
        log.info("确认完成订单 id={}", id);
        orderService.confirm(id);
        return Result.success();
    }

    @ApiLog("取消订单")
    @Operation(summary = "取消订单")
    @PutMapping("/{id}/cancel")
    public Result<String> cancel(@PathVariable Long id) {
        log.info("取消订单 id={}", id);
        orderService.cancel(id);
        return Result.success();
    }

    @ApiLog("查订单")
    @Operation(summary = "分页查询订单")
    @GetMapping
    public Result<PageResult> pageQuery(OrdersPageQueryDTO dto) {
        log.info("分页查询订单：{}", dto);
        PageResult pageResult = orderService.pageQuery(dto);
        return Result.success(pageResult);
    }

    @ApiLog("订单详情")
    @Operation(summary = "订单详情")
    @GetMapping("/{id}")
    public Result<OrderVO> getById(@PathVariable Long id) {
        log.info("查询订单详情 id={}", id);
        OrderVO orderVO = orderService.getById(id);
        return Result.success(orderVO);
    }
}
