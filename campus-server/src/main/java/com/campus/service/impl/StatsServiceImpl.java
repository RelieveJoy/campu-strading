package com.campus.service.impl;

import com.campus.mapper.StatsMapper;
import com.campus.service.StatsService;
import com.campus.vo.OrderStatisticsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class StatsServiceImpl implements StatsService {

    @Autowired
    private StatsMapper statsMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<OrderStatisticsVO> countByCategory() {
        String key = "cache:stats:category";
        List<OrderStatisticsVO> cached = (List<OrderStatisticsVO>) redisTemplate.opsForValue().get(key);
        if (cached != null) {
            return cached;
        }
        List<OrderStatisticsVO> list = statsMapper.countByCategory();
        redisTemplate.opsForValue().set(key, list, 10, TimeUnit.MINUTES);
        return list;
    }

    @Override
    public List<OrderStatisticsVO> countByPriceRange() {
        String key = "cache:stats:price_range";
        List<OrderStatisticsVO> cached = (List<OrderStatisticsVO>) redisTemplate.opsForValue().get(key);
        if (cached != null) {
            return cached;
        }
        List<OrderStatisticsVO> list = statsMapper.countByPriceRange();
        redisTemplate.opsForValue().set(key, list, 10, TimeUnit.MINUTES);
        return list;
    }

    @Override
    public List<OrderStatisticsVO> countTradeByDay() {
        return statsMapper.countTradeByDay();
    }
}
