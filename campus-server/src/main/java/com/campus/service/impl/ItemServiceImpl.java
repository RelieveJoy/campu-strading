package com.campus.service.impl;

import com.campus.annotation.CacheInvalidate;
import com.campus.constant.MessageConstant;
import com.campus.context.BaseContext;
import com.campus.dto.ItemDTO;
import com.campus.dto.ItemPageQueryDTO;
import com.campus.entity.Item;
import com.campus.exception.ItemBusinessException;
import com.campus.mapper.ItemMapper;
import com.campus.result.PageResult;
import com.campus.service.ItemService;
import com.campus.vo.ItemDetailVO;
import com.campus.vo.ItemVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    @CacheInvalidate
    public void add(ItemDTO itemDTO) {
        Item item = new Item();
        BeanUtils.copyProperties(itemDTO, item);
        item.setSellerId(BaseContext.getCurrentId());
        item.setStatus(1);
        item.setViewCount(0);
        itemMapper.insert(item);
    }

    @Override
    @CacheInvalidate
    public void update(Long itemId, ItemDTO itemDTO) {
        Item item = itemMapper.getById(itemId);
        if (item == null) {
            throw new ItemBusinessException(MessageConstant.ITEM_NOT_FOUND);
        }
        if (!item.getSellerId().equals(BaseContext.getCurrentId())) {
            throw new ItemBusinessException("只能修改自己的商品");
        }
        BeanUtils.copyProperties(itemDTO, item);
        item.setItemId(itemId);
        itemMapper.update(item);
    }

    @Override
    @CacheInvalidate
    public void delete(Long itemId) {
        Item item = itemMapper.getById(itemId);
        if (item == null) {
            throw new ItemBusinessException(MessageConstant.ITEM_NOT_FOUND);
        }
        if (!item.getSellerId().equals(BaseContext.getCurrentId())) {
            throw new ItemBusinessException("只能下架自己的商品");
        }
        item.setStatus(0);
        itemMapper.update(item);
    }

    @Override
    @CacheInvalidate
    public void relist(Long itemId) {
        Item item = itemMapper.getById(itemId);
        if (item == null) {
            throw new ItemBusinessException(MessageConstant.ITEM_NOT_FOUND);
        }
        if (!item.getSellerId().equals(BaseContext.getCurrentId())) {
            throw new ItemBusinessException("只能上架自己的商品");
        }
        if (item.getStatus() != 0) {
            throw new ItemBusinessException("该商品不是下架状态，无需上架");
        }
        item.setStatus(1);
        itemMapper.update(item);
    }

    @Override
    public PageResult pageQuery(ItemPageQueryDTO dto) {
        PageHelper.startPage(dto.getPage(), dto.getPageSize());
        List<ItemVO> list = itemMapper.pageQuery(dto);
        Page<ItemVO> page = (Page<ItemVO>) list;
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public ItemDetailVO getById(Long itemId) {
        ItemDetailVO detail = itemMapper.getDetailById(itemId);
        if (detail == null) {
            throw new ItemBusinessException(MessageConstant.ITEM_NOT_FOUND);
        }
        return detail;
    }

    @Override
    public List<ItemVO> getByUserId(Long userId) {
        return itemMapper.getByUserId(userId);
    }

    @Override
    public List<ItemVO> getHomeItems() {
        String key = "cache:home:items";
        List<ItemVO> cached = (List<ItemVO>) redisTemplate.opsForValue().get(key);
        if (cached != null) {
            return cached;
        }
        ItemPageQueryDTO dto = new ItemPageQueryDTO();
        dto.setPage(1);
        dto.setPageSize(20);
        dto.setStatus(1);
        PageHelper.startPage(dto.getPage(), dto.getPageSize());
        List<ItemVO> list = itemMapper.pageQuery(dto);

        if (list.isEmpty()) {
            // 穿透防护：空结果也缓存，TTL 较短（1~2 分钟），防止反复查库
            long ttl = 1 + ThreadLocalRandom.current().nextLong(1);
            redisTemplate.opsForValue().set(key, list, ttl, TimeUnit.MINUTES);
        } else {
            // 雪崩防护：TTL 加随机偏移（5~6 分钟），避免大量缓存同时过期
            long ttl = 5 + ThreadLocalRandom.current().nextLong(1);
            redisTemplate.opsForValue().set(key, list, ttl, TimeUnit.MINUTES);
        }
        return list;
    }
}
