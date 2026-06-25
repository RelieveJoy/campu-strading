package com.campus.mapper;

import com.campus.entity.Review;
import com.campus.vo.ItemRatingVO;
import com.campus.vo.ReviewVO;
import com.campus.vo.UserRatingVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ReviewMapper {

    @Insert("insert into review(order_id, item_id, reviewer_id, target_id, rating, content, create_time) " +
            "values (#{orderId}, #{itemId}, #{reviewerId}, #{targetId}, #{rating}, #{content}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "reviewId")
    void insert(Review review);

    @Select("select * from review where order_id = #{orderId}")
    Review getByOrderId(Long orderId);

    @Select("select * from review where review_id = #{reviewId}")
    Review getById(Long reviewId);

    @Select("select * from review where item_id = #{itemId} and reviewer_id = #{reviewerId} limit 1")
    Review getByItemIdAndReviewerId(Long itemId, Long reviewerId);

    @Update("update review set rating = #{rating}, content = #{content} where review_id = #{reviewId}")
    void update(Review review);

    List<ReviewVO> listByItemId(Long itemId);

    List<ReviewVO> listByTargetId(Long targetId);

    ItemRatingVO avgRatingByItemId(Long itemId);

    UserRatingVO avgRatingBySellerId(Long sellerId);
}
