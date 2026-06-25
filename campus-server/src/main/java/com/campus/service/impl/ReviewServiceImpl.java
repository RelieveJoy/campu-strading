package com.campus.service.impl;

import com.campus.constant.MessageConstant;
import com.campus.context.BaseContext;
import com.campus.dto.ReviewDTO;
import com.campus.entity.Orders;
import com.campus.entity.Review;
import com.campus.exception.OrderBusinessException;
import com.campus.mapper.OrderMapper;
import com.campus.mapper.ReviewMapper;
import com.campus.result.PageResult;
import com.campus.service.ReviewService;
import com.campus.vo.ItemRatingVO;
import com.campus.vo.ReviewVO;
import com.campus.vo.UserRatingVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewMapper reviewMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Override
    @Transactional
    public void submit(ReviewDTO dto) {
        Long currentUserId = BaseContext.getCurrentId();

        Orders order = orderMapper.getById(dto.getOrderId());
        if (order == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        if (!order.getBuyerId().equals(currentUserId)) {
            throw new OrderBusinessException("只有买家可以评价");
        }
        if (order.getStatus() != Orders.COMPLETED && order.getStatus() != Orders.CANCELLED) {
            throw new OrderBusinessException("订单待确认，还不能评价");
        }

        Review existing = reviewMapper.getByOrderId(dto.getOrderId());
        if (existing != null) {
            throw new OrderBusinessException("该订单已评价过");
        }

        Review sameItem = reviewMapper.getByItemIdAndReviewerId(dto.getItemId(), currentUserId);
        if (sameItem != null) {
            throw new OrderBusinessException("您已评价过该商品，同一商品只能评价一次");
        }

        Review review = Review.builder()
                .orderId(dto.getOrderId())
                .itemId(dto.getItemId())
                .reviewerId(currentUserId)
                .targetId(order.getSellerId())
                .rating(dto.getRating())
                .content(dto.getContent())
                .build();
        reviewMapper.insert(review);

        log.info("用户 {} 对订单 {} 评分: {}", currentUserId, dto.getOrderId(), dto.getRating());
    }

    @Override
    @Transactional
    public void update(Long reviewId, ReviewDTO dto) {
        Long currentUserId = BaseContext.getCurrentId();

        Review review = reviewMapper.getById(reviewId);
        if (review == null) {
            throw new OrderBusinessException("评价不存在");
        }
        if (!review.getReviewerId().equals(currentUserId)) {
            throw new OrderBusinessException("只有评价人本人可以修改评价");
        }

        review.setRating(dto.getRating());
        review.setContent(dto.getContent());
        reviewMapper.update(review);

        log.info("用户 {} 修改了评价 {}，新评分: {}", currentUserId, reviewId, dto.getRating());
    }

    @Override
    public List<ReviewVO> getByItem(Long itemId) {
        return reviewMapper.listByItemId(itemId);
    }

    @Override
    public ItemRatingVO getItemRating(Long itemId) {
        return reviewMapper.avgRatingByItemId(itemId);
    }

    @Override
    public UserRatingVO getUserRating(Long userId) {
        return reviewMapper.avgRatingBySellerId(userId);
    }

    @Override
    public PageResult getByTargetUser(Long userId, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        List<ReviewVO> list = reviewMapper.listByTargetId(userId);
        Page<ReviewVO> p = (Page<ReviewVO>) list;
        return new PageResult(p.getTotal(), p.getResult());
    }
}
