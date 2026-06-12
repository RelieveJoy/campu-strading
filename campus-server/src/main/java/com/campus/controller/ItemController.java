package com.campus.controller;

import com.campus.dto.ItemDTO;
import com.campus.dto.ItemPageQueryDTO;
import com.campus.result.PageResult;
import com.campus.result.Result;
import com.campus.service.ItemService;
import com.campus.vo.ItemDetailVO;
import com.campus.vo.ItemVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/items")
@Slf4j
@Tag(name = "商品相关接口")
public class ItemController {

    @Autowired
    private ItemService itemService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Operation(summary = "发布商品")
    @PostMapping
    public Result<String> add(@RequestBody @Valid ItemDTO itemDTO) {
        log.info("发布商品：{}", itemDTO);
        itemService.add(itemDTO);
        return Result.success();
    }

    @Operation(summary = "编辑商品")
    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Long id, @RequestBody @Valid ItemDTO itemDTO) {
        log.info("编辑商品 id={}：{}", id, itemDTO);
        itemService.update(id, itemDTO);
        return Result.success();
    }

    @Operation(summary = "下架商品")
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        log.info("下架商品 id={}", id);
        itemService.delete(id);
        return Result.success();
    }

    @Operation(summary = "重新上架商品")
    @PutMapping("/{id}/relist")
    public Result<String> relist(@PathVariable Long id) {
        log.info("重新上架商品 id={}", id);
        itemService.relist(id);
        return Result.success();
    }

    @Operation(summary = "分页查询商品")
    @GetMapping
    public Result<PageResult> pageQuery(ItemPageQueryDTO dto) {
        log.info("分页查询商品：{}", dto);
        PageResult pageResult = itemService.pageQuery(dto);
        return Result.success(pageResult);
    }

    @Operation(summary = "首页商品列表（缓存）")
    @GetMapping("/home")
    public Result<List<ItemVO>> homeItems() {
        log.info("查询首页商品");
        List<ItemVO> list = itemService.getHomeItems();
        return Result.success(list);
    }

    @Operation(summary = "商品详情")
    @GetMapping("/{id}")
    public Result<ItemDetailVO> getById(@PathVariable Long id) {
        log.info("查询商品详情 id={}", id);
        stringRedisTemplate.opsForValue().increment("view_count:" + id);
        ItemDetailVO detail = itemService.getById(id);
        return Result.success(detail);
    }
}
