package com.campus.controller;

import com.campus.dto.OrdersDTO;
import com.campus.dto.OrdersPageQueryDTO;
import com.campus.result.PageResult;
import com.campus.result.Result;
import com.campus.service.OrderService;
import com.campus.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@Slf4j
@Api(tags = "订单相关接口")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @ApiOperation("创建订单")
    @PostMapping
    public Result<String> create(@RequestBody OrdersDTO ordersDTO) {
        log.info("创建订单：{}", ordersDTO);
        orderService.create(ordersDTO);
        return Result.success();
    }

    @ApiOperation("确认完成订单")
    @PutMapping("/{id}/confirm")
    public Result<String> confirm(@PathVariable Long id) {
        log.info("确认完成订单 id={}", id);
        orderService.confirm(id);
        return Result.success();
    }

    @ApiOperation("取消订单")
    @PutMapping("/{id}/cancel")
    public Result<String> cancel(@PathVariable Long id) {
        log.info("取消订单 id={}", id);
        orderService.cancel(id);
        return Result.success();
    }

    @ApiOperation("分页查询订单")
    @GetMapping
    public Result<PageResult> pageQuery(OrdersPageQueryDTO dto) {
        log.info("分页查询订单：{}", dto);
        PageResult pageResult = orderService.pageQuery(dto);
        return Result.success(pageResult);
    }

    @ApiOperation("订单详情")
    @GetMapping("/{id}")
    public Result<OrderVO> getById(@PathVariable Long id) {
        log.info("查询订单详情 id={}", id);
        OrderVO orderVO = orderService.getById(id);
        return Result.success(orderVO);
    }
}
