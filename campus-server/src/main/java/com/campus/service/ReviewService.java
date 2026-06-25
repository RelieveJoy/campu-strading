package com.campus.service;

import com.campus.dto.ReviewDTO;
import com.campus.result.PageResult;
import com.campus.vo.ItemRatingVO;
import com.campus.vo.ReviewVO;
import com.campus.vo.UserRatingVO;

import java.util.List;

public interface ReviewService {

    void submit(ReviewDTO reviewDTO);

    void update(Long reviewId, ReviewDTO reviewDTO);

    List<ReviewVO> getByItem(Long itemId);

    ItemRatingVO getItemRating(Long itemId);

    UserRatingVO getUserRating(Long userId);

    PageResult getByTargetUser(Long userId, int page, int pageSize);
}
