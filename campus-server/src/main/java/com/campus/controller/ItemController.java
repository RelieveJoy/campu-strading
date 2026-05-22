package com.campus.controller;

import com.campus.dto.ItemDTO;
import com.campus.dto.ItemPageQueryDTO;
import com.campus.result.PageResult;
import com.campus.result.Result;
import com.campus.service.ItemService;
import com.campus.vo.ItemDetailVO;
import com.campus.vo.ItemVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@Slf4j
@Api(tags = "商品相关接口")
public class ItemController {

    @Autowired
    private ItemService itemService;
    @Autowired
    private RedisTemplate redisTemplate;

    @ApiOperation("发布商品")
    @PostMapping
    public Result<String> add(@RequestBody ItemDTO itemDTO) {
        log.info("发布商品：{}", itemDTO);
        itemService.add(itemDTO);
        return Result.success();
    }

    @ApiOperation("编辑商品")
    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Long id, @RequestBody ItemDTO itemDTO) {
        log.info("编辑商品 id={}：{}", id, itemDTO);
        itemService.update(id, itemDTO);
        return Result.success();
    }

    @ApiOperation("下架商品")
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        log.info("下架商品 id={}", id);
        itemService.delete(id);
        return Result.success();
    }

    @ApiOperation("重新上架商品")
    @PutMapping("/{id}/relist")
    public Result<String> relist(@PathVariable Long id) {
        log.info("重新上架商品 id={}", id);
        itemService.relist(id);
        return Result.success();
    }

    @ApiOperation("分页查询商品")
    @GetMapping
    public Result<PageResult> pageQuery(ItemPageQueryDTO dto) {
        log.info("分页查询商品：{}", dto);
        PageResult pageResult = itemService.pageQuery(dto);
        return Result.success(pageResult);
    }

    @ApiOperation("商品详情")
    @GetMapping("/{id}")
    public Result<ItemDetailVO> getById(@PathVariable Long id) {
        log.info("查询商品详情 id={}", id);
        redisTemplate.opsForValue().increment("view_count:" + id);
        ItemDetailVO detail = itemService.getById(id);
        return Result.success(detail);
    }
}
