package com.campus.service;

import com.campus.vo.OrderStatisticsVO;

import java.util.List;

public interface StatsService {

    List<OrderStatisticsVO> countByCategory();

    List<OrderStatisticsVO> countByPriceRange();

    List<OrderStatisticsVO> countTradeByDay();
}
