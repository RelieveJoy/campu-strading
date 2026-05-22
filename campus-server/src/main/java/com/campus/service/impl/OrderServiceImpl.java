package com.campus.service.impl;

import com.campus.constant.MessageConstant;
import com.campus.context.BaseContext;
import com.campus.dto.OrdersDTO;
import com.campus.dto.OrdersPageQueryDTO;
import com.campus.entity.Item;
import com.campus.entity.Orders;
import com.campus.exception.OrderBusinessException;
import com.campus.mapper.ItemMapper;
import com.campus.mapper.OrderMapper;
import com.campus.result.PageResult;
import com.campus.service.OrderService;
import com.campus.vo.OrderVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private ItemMapper itemMapper;

    @Override
    @Transactional
    public void create(OrdersDTO ordersDTO) {
        Long buyerId = BaseContext.getCurrentId();
        Item item = itemMapper.getById(ordersDTO.getItemId());
        if (item == null) {
            throw new OrderBusinessException(MessageConstant.ITEM_NOT_FOUND);
        }
        if (item.getStatus() != 1) {
            throw new OrderBusinessException("该商品已不在售");
        }
        if (item.getSellerId().equals(buyerId)) {
            throw new OrderBusinessException("不能购买自己的商品");
        }

        Orders order = Orders.builder()
                .itemId(item.getItemId())
                .buyerId(buyerId)
                .sellerId(item.getSellerId())
                .amount(item.getPrice())
                .status(Orders.TO_BE_CONFIRMED)
                .build();
        orderMapper.insert(order);

        item.setStatus(2);
        itemMapper.update(item);
    }

    @Override
    public void confirm(Long orderId) {
        Orders order = orderMapper.getById(orderId);
        if (order == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        if (!order.getSellerId().equals(BaseContext.getCurrentId())) {
            throw new OrderBusinessException("仅卖家可确认完成订单");
        }
        if (order.getStatus() != Orders.TO_BE_CONFIRMED) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        order.setStatus(Orders.COMPLETED);
        orderMapper.update(order);
    }

    @Override
    @Transactional
    public void cancel(Long orderId) {
        Orders order = orderMapper.getById(orderId);
        if (order == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        Long currentUserId = BaseContext.getCurrentId();
        if (!order.getBuyerId().equals(currentUserId) && !order.getSellerId().equals(currentUserId)) {
            throw new OrderBusinessException("无权取消该订单");
        }
        if (order.getStatus() != Orders.TO_BE_CONFIRMED) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        order.setStatus(Orders.CANCELLED);
        orderMapper.update(order);

        Item item = itemMapper.getById(order.getItemId());
        item.setStatus(1);
        itemMapper.update(item);
    }

    @Override
    public PageResult pageQuery(OrdersPageQueryDTO dto) {
        PageHelper.startPage(dto.getPage(), dto.getPageSize());
        List<OrderVO> list = orderMapper.pageQuery(dto);
        Page<OrderVO> page = (Page<OrderVO>) list;
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public OrderVO getById(Long orderId) {
        OrderVO orderVO = orderMapper.getDetailById(orderId);
        if (orderVO == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        return orderVO;
    }
}
