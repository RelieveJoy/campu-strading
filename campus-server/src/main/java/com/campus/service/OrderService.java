package com.campus.service;

import com.campus.dto.OrdersDTO;
import com.campus.dto.OrdersPageQueryDTO;
import com.campus.result.PageResult;
import com.campus.vo.OrderVO;

public interface OrderService {

    void create(OrdersDTO ordersDTO);

    void confirm(Long orderId);

    void cancel(Long orderId);

    PageResult pageQuery(OrdersPageQueryDTO dto);

    OrderVO getById(Long orderId);
}
