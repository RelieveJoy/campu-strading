package com.campus.service;

import com.campus.context.BaseContext;
import com.campus.dto.ItemDTO;
import com.campus.dto.OrdersDTO;
import com.campus.dto.OrdersPageQueryDTO;
import com.campus.entity.Orders;
import com.campus.entity.User;
import com.campus.mapper.OrderMapper;
import com.campus.mapper.UserMapper;
import com.campus.result.PageResult;
import com.campus.vo.OrderVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Sql(scripts = "/schema.sql")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class OrderServiceTest {

    @Autowired private OrderService orderService;
    @Autowired private ItemService itemService;
    @Autowired private UserService userService;
    @Autowired private UserMapper userMapper;
    @Autowired private OrderMapper orderMapper;
    @Autowired private PasswordEncoder passwordEncoder;

    private Long orderId;
    private Long buyerId;
    private Long sellerId;

    @BeforeEach
    void setUp() {
        userService.register(buildUserDTO("seller01", "卖家"));
        userService.register(buildUserDTO("buyer01", "买家"));
        sellerId = userMapper.getByStudentId("seller01").getUserId();
        buyerId = userMapper.getByStudentId("buyer01").getUserId();

        // 卖家发商品
        BaseContext.setCurrentId(sellerId);
        ItemDTO itemDTO = new ItemDTO();
        itemDTO.setTitle("测试商品");
        itemDTO.setDescription("test");
        itemDTO.setPrice(new BigDecimal("100"));
        itemDTO.setCategoryId(1L);
        itemDTO.setImageUrl("https://example.com/img.jpg");
        itemService.add(itemDTO);
        Long itemId = itemService.getByUserId(sellerId).get(0).getItemId();

        // 买家下单
        BaseContext.setCurrentId(buyerId);
        OrdersDTO dto = new OrdersDTO();
        dto.setItemId(itemId);
        orderService.create(dto);

        // 查订单ID
        OrdersPageQueryDTO q = new OrdersPageQueryDTO();
        q.setPage(1);
        q.setPageSize(10);
        q.setBuyerId(buyerId);
        List<OrderVO> records = orderService.pageQuery(q).getRecords();
        orderId = records.get(0).getOrderId();
    }

    @Test
    @DisplayName("买家取消订单成功，商品恢复在售")
    void cancelOrderRestoresItem() {
        BaseContext.setCurrentId(buyerId);
        orderService.cancel(orderId);
        Orders order = orderMapper.getById(orderId);
        assertEquals(Orders.CANCELLED, order.getStatus());
    }

    @Test
    @DisplayName("卖家确认完成订单成功")
    void confirmOrderSuccess() {
        BaseContext.setCurrentId(sellerId);
        orderService.confirm(orderId);
        Orders order = orderMapper.getById(orderId);
        assertEquals(Orders.COMPLETED, order.getStatus());
    }

    @Test
    @DisplayName("分页查询订单")
    void pageQueryReturnsPage() {
        BaseContext.setCurrentId(buyerId);
        OrdersPageQueryDTO q = new OrdersPageQueryDTO();
        q.setPage(1);
        q.setPageSize(10);
        PageResult result = orderService.pageQuery(q);
        assertTrue(result.getTotal() >= 1);
    }

    @Test
    @DisplayName("查询订单详情")
    void getByIdReturnsDetail() {
        OrderVO vo = orderService.getById(orderId);
        assertNotNull(vo);
        assertEquals(buyerId, vo.getBuyerId());
    }

    private com.campus.dto.UserRegisterDTO buildUserDTO(String sid, String name) {
        com.campus.dto.UserRegisterDTO dto = new com.campus.dto.UserRegisterDTO();
        dto.setStudentId(sid);
        dto.setUsername(name);
        dto.setPassword("123456");
        dto.setPhone("13800001111");
        return dto;
    }
}
