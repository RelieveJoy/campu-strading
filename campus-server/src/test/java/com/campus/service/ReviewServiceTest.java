package com.campus.service;

import com.campus.context.BaseContext;
import com.campus.dto.ItemDTO;
import com.campus.dto.OrdersDTO;
import com.campus.dto.OrdersPageQueryDTO;
import com.campus.dto.ReviewDTO;
import com.campus.dto.UserRegisterDTO;
import com.campus.entity.Orders;
import com.campus.exception.OrderBusinessException;
import com.campus.mapper.OrderMapper;
import com.campus.mapper.ReviewMapper;
import com.campus.mapper.UserMapper;
import com.campus.vo.ItemRatingVO;
import com.campus.vo.ItemVO;
import com.campus.vo.ReviewVO;
import com.campus.vo.UserRatingVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Sql(scripts = "/schema.sql")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ReviewServiceTest {

    @Autowired private ReviewService reviewService;
    @Autowired private ItemService itemService;
    @Autowired private OrderService orderService;
    @Autowired private UserService userService;
    @Autowired private UserMapper userMapper;
    @Autowired private OrderMapper orderMapper;
    @Autowired private ReviewMapper reviewMapper;

    private Long orderId;
    private Long itemId;
    private Long buyerId;
    private Long sellerId;

    @BeforeEach
    void setUp() {
        userService.register(buildUserDTO("seller01", "卖家"));
        userService.register(buildUserDTO("buyer01", "买家"));
        sellerId = userMapper.getByStudentId("seller01").getUserId();
        buyerId = userMapper.getByStudentId("buyer01").getUserId();

        BaseContext.setCurrentId(sellerId);
        ItemDTO itemDTO = new ItemDTO();
        itemDTO.setTitle("测试商品");
        itemDTO.setDescription("test");
        itemDTO.setPrice(new BigDecimal("100"));
        itemDTO.setCategoryId(1L);
        itemDTO.setImageUrl("https://example.com/img.jpg");
        itemService.add(itemDTO);
        var items = itemService.getByUserId(sellerId, null, 1, 10).getRecords();
        itemId = ((ItemVO) items.get(0)).getItemId();

        BaseContext.setCurrentId(buyerId);
        OrdersDTO dto = new OrdersDTO();
        dto.setItemId(itemId);
        orderService.create(dto);

        OrdersPageQueryDTO q = new OrdersPageQueryDTO();
        q.setPage(1);
        q.setPageSize(10);
        q.setBuyerId(buyerId);
        List<com.campus.vo.OrderVO> records = orderService.pageQuery(q).getRecords();
        orderId = records.get(0).getOrderId();
    }

    // ── Submit ──

    @Test
    @DisplayName("买家在已完成订单上提交评价成功")
    void submitSuccess() {
        BaseContext.setCurrentId(sellerId);
        orderService.confirm(orderId);

        BaseContext.setCurrentId(buyerId);
        ReviewDTO dto = new ReviewDTO();
        dto.setOrderId(orderId);
        dto.setItemId(itemId);
        dto.setRating(4);
        dto.setContent("不错");
        assertDoesNotThrow(() -> reviewService.submit(dto));
    }

    @Test
    @DisplayName("买家在已取消订单上提交评价成功")
    void submitOnCancelledOrder() {
        BaseContext.setCurrentId(buyerId);
        orderService.cancel(orderId);

        ReviewDTO dto = new ReviewDTO();
        dto.setOrderId(orderId);
        dto.setItemId(itemId);
        dto.setRating(3);
        assertDoesNotThrow(() -> reviewService.submit(dto));
    }

    @Test
    @DisplayName("买家选择不评分(rating=0)")
    void submitWithZeroRating() {
        BaseContext.setCurrentId(sellerId);
        orderService.confirm(orderId);

        BaseContext.setCurrentId(buyerId);
        ReviewDTO dto = new ReviewDTO();
        dto.setOrderId(orderId);
        dto.setItemId(itemId);
        dto.setRating(0);
        assertDoesNotThrow(() -> reviewService.submit(dto));

        ItemRatingVO rating = reviewService.getItemRating(itemId);
        assertEquals(0, BigDecimal.ZERO.compareTo(rating.getAverageRating()));
        assertEquals(0, rating.getReviewCount());
    }

    @Test
    @DisplayName("待确认订单不能评价")
    void pendingOrderCannotReview() {
        BaseContext.setCurrentId(buyerId);
        ReviewDTO dto = new ReviewDTO();
        dto.setOrderId(orderId);
        dto.setItemId(itemId);
        dto.setRating(4);
        assertThrows(OrderBusinessException.class, () -> reviewService.submit(dto));
    }

    @Test
    @DisplayName("非买家不能评价")
    void nonBuyerCannotReview() {
        BaseContext.setCurrentId(sellerId);
        orderService.confirm(orderId);

        BaseContext.setCurrentId(sellerId);
        ReviewDTO dto = new ReviewDTO();
        dto.setOrderId(orderId);
        dto.setItemId(itemId);
        dto.setRating(4);
        assertThrows(OrderBusinessException.class, () -> reviewService.submit(dto));
    }

    @Test
    @DisplayName("同一订单不能重复评价")
    void duplicateReviewThrows() {
        BaseContext.setCurrentId(sellerId);
        orderService.confirm(orderId);

        BaseContext.setCurrentId(buyerId);
        ReviewDTO dto = new ReviewDTO();
        dto.setOrderId(orderId);
        dto.setItemId(itemId);
        dto.setRating(4);
        reviewService.submit(dto);

        ReviewDTO dto2 = new ReviewDTO();
        dto2.setOrderId(orderId);
        dto2.setItemId(itemId);
        dto2.setRating(5);
        assertThrows(OrderBusinessException.class, () -> reviewService.submit(dto2));
    }

    @Test
    @DisplayName("同买家同商品不能重复评价（防刷分）")
    void sameBuyerSameItemCannotReviewTwice() {
        // 买家取消订单，商品恢复在售
        BaseContext.setCurrentId(buyerId);
        orderService.cancel(orderId);

        // 提交第一次评价
        ReviewDTO dto = new ReviewDTO();
        dto.setOrderId(orderId);
        dto.setItemId(itemId);
        dto.setRating(4);
        reviewService.submit(dto);

        // 买家重新购买同一商品
        OrdersDTO order2 = new OrdersDTO();
        order2.setItemId(itemId);
        orderService.create(order2);

        // 找新订单ID
        var records = orderService.pageQuery(
                new com.campus.dto.OrdersPageQueryDTO() {{
                    setPage(1); setPageSize(10); setBuyerId(buyerId);
                }}).getRecords();
        Long order2Id = ((com.campus.vo.OrderVO) records.get(0)).getOrderId();
        orderService.cancel(order2Id);

        // 尝试再评价同一商品，应抛异常
        ReviewDTO dto2 = new ReviewDTO();
        dto2.setOrderId(order2Id);
        dto2.setItemId(itemId);
        dto2.setRating(3);
        assertThrows(OrderBusinessException.class, () -> reviewService.submit(dto2));
    }

    @Test
    @DisplayName("订单不存在则抛异常")
    void orderNotFoundThrows() {
        BaseContext.setCurrentId(buyerId);
        ReviewDTO dto = new ReviewDTO();
        dto.setOrderId(99999L);
        dto.setItemId(itemId);
        dto.setRating(4);
        assertThrows(OrderBusinessException.class, () -> reviewService.submit(dto));
    }

    // ── Query ──

    @Test
    @DisplayName("查询商品评价列表")
    void getByItemReturnsReviews() {
        BaseContext.setCurrentId(sellerId);
        orderService.confirm(orderId);

        BaseContext.setCurrentId(buyerId);
        ReviewDTO dto = new ReviewDTO();
        dto.setOrderId(orderId);
        dto.setItemId(itemId);
        dto.setRating(5);
        dto.setContent("很好");
        reviewService.submit(dto);

        List<ReviewVO> reviews = reviewService.getByItem(itemId);
        assertEquals(1, reviews.size());
        assertEquals(5, reviews.get(0).getRating());
        assertEquals("买家", reviews.get(0).getReviewerName());
    }

    @Test
    @DisplayName("商品评分摘要")
    void getItemRating() {
        BaseContext.setCurrentId(sellerId);
        orderService.confirm(orderId);

        BaseContext.setCurrentId(buyerId);
        ReviewDTO dto = new ReviewDTO();
        dto.setOrderId(orderId);
        dto.setItemId(itemId);
        dto.setRating(4);
        reviewService.submit(dto);

        ItemRatingVO rating = reviewService.getItemRating(itemId);
        assertNotNull(rating);
        assertEquals(0, new BigDecimal("4.0").compareTo(rating.getAverageRating()));
        assertEquals(1, rating.getReviewCount());
    }

    @Test
    @DisplayName("无评价商品评分摘要为0")
    void getItemRatingNoReviews() {
        ItemRatingVO rating = reviewService.getItemRating(itemId);
        assertEquals(0, BigDecimal.ZERO.compareTo(rating.getAverageRating()));
        assertEquals(0, rating.getReviewCount());
    }

    @Test
    @DisplayName("用户综合评分")
    void getUserRating() {
        BaseContext.setCurrentId(sellerId);
        orderService.confirm(orderId);

        BaseContext.setCurrentId(buyerId);
        ReviewDTO dto = new ReviewDTO();
        dto.setOrderId(orderId);
        dto.setItemId(itemId);
        dto.setRating(5);
        reviewService.submit(dto);

        UserRatingVO rating = reviewService.getUserRating(sellerId);
        assertNotNull(rating);
        assertEquals(0, new BigDecimal("5.0").compareTo(rating.getOverallRating()));
        assertEquals(1, rating.getRatedItemCount());
    }

    @Test
    @DisplayName("无评价用户综合评分为0")
    void getUserRatingNoReviews() {
        UserRatingVO rating = reviewService.getUserRating(sellerId);
        assertEquals(0, BigDecimal.ZERO.compareTo(rating.getOverallRating()));
        assertEquals(0, rating.getRatedItemCount());
    }

    // ── Update ──

    @Test
    @DisplayName("买家修改评分，用户综评随之变化")
    void updateRatingChangesUserRating() {
        BaseContext.setCurrentId(sellerId);
        orderService.confirm(orderId);

        BaseContext.setCurrentId(buyerId);
        ReviewDTO dto = new ReviewDTO();
        dto.setOrderId(orderId);
        dto.setItemId(itemId);
        dto.setRating(2);
        dto.setContent("一般");
        reviewService.submit(dto);

        UserRatingVO before = reviewService.getUserRating(sellerId);
        assertEquals(0, new BigDecimal("2.0").compareTo(before.getOverallRating()));

        Long reviewId = reviewMapper.getByOrderId(orderId).getReviewId();
        ReviewDTO updateDto = new ReviewDTO();
        updateDto.setOrderId(orderId);
        updateDto.setItemId(itemId);
        updateDto.setRating(5);
        updateDto.setContent("改评价，其实很好");
        reviewService.update(reviewId, updateDto);

        UserRatingVO after = reviewService.getUserRating(sellerId);
        assertEquals(0, new BigDecimal("5.0").compareTo(after.getOverallRating()));
    }

    @Test
    @DisplayName("非评价人修改评价抛异常")
    void nonReviewerCannotUpdate() {
        BaseContext.setCurrentId(sellerId);
        orderService.confirm(orderId);

        BaseContext.setCurrentId(buyerId);
        ReviewDTO dto = new ReviewDTO();
        dto.setOrderId(orderId);
        dto.setItemId(itemId);
        dto.setRating(3);
        reviewService.submit(dto);

        BaseContext.setCurrentId(sellerId);
        Long reviewId = reviewMapper.getByOrderId(orderId).getReviewId();
        ReviewDTO updateDto = new ReviewDTO();
        updateDto.setOrderId(orderId);
        updateDto.setItemId(itemId);
        updateDto.setRating(1);
        assertThrows(OrderBusinessException.class, () -> reviewService.update(reviewId, updateDto));
    }

    @Test
    @DisplayName("修改不存在的评价抛异常")
    void updateNonExistentReviewThrows() {
        BaseContext.setCurrentId(buyerId);
        ReviewDTO dto = new ReviewDTO();
        dto.setOrderId(orderId);
        dto.setItemId(itemId);
        dto.setRating(3);
        assertThrows(OrderBusinessException.class, () -> reviewService.update(99999L, dto));
    }

    private UserRegisterDTO buildUserDTO(String sid, String name) {
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setStudentId(sid);
        dto.setUsername(name);
        dto.setPassword("123456");
        dto.setPhone("13800001111");
        return dto;
    }
}
