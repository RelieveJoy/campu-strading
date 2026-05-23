package com.campus.service.impl;

import com.campus.mapper.StatsMapper;
import com.campus.service.StatsService;
import com.campus.vo.OrderStatisticsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatsServiceImpl implements StatsService {

    @Autowired
    private StatsMapper statsMapper;

    @Override
    public List<OrderStatisticsVO> countByCategory() {
        return statsMapper.countByCategory();
    }

    @Override
    public List<OrderStatisticsVO> countByPriceRange() {
        return statsMapper.countByPriceRange();
    }

    @Override
    public List<OrderStatisticsVO> countTradeByDay() {
        return statsMapper.countTradeByDay();
    }
}
