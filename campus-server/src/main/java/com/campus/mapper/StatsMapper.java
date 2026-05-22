package com.campus.mapper;

import com.campus.vo.OrderStatisticsVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StatsMapper {

    List<OrderStatisticsVO> countByCategory();

    List<OrderStatisticsVO> countByPriceRange();

    List<OrderStatisticsVO> countTradeByDay();
}
