package com.campus.service;

import com.campus.context.BaseContext;
import com.campus.dto.OrdersDTO;
import com.campus.entity.Item;
import com.campus.entity.User;
import com.campus.exception.OrderBusinessException;
import com.campus.mapper.ItemMapper;
import com.campus.mapper.OrderMapper;
import com.campus.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Sql(scripts = "/schema.sql")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class OrderServiceConcurrentTest {

    @Autowired private OrderService orderService;
    @Autowired private ItemMapper itemMapper;
    @Autowired private UserMapper userMapper;
    @Autowired private OrderMapper orderMapper;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JdbcTemplate jdbcTemplate;

    private Long itemId;
    private static final int BUYER_COUNT = 10;

    @BeforeEach
    void setUp() {
        User seller = User.builder()
                .studentId("seller001").username("卖家")
                .password(passwordEncoder.encode("123456")).status(1).build();
        userMapper.insert(seller);
        Long sellerId = userMapper.getByStudentId("seller001").getUserId();

        Item item = Item.builder()
                .sellerId(sellerId).title("热门教材")
                .description("一本好书").price(new BigDecimal("50.00"))
                .status(1).viewCount(0).build();
        itemMapper.insert(item);
        // 自增 ID 不回写实体，从库查最新 item
        itemId = jdbcTemplate.queryForObject(
                "SELECT item_id FROM item ORDER BY item_id DESC LIMIT 1", Long.class);

        for (int i = 0; i < BUYER_COUNT; i++) {
            User buyer = User.builder()
                    .studentId("buyer" + String.format("%03d", i))
                    .username("买家" + i)
                    .password(passwordEncoder.encode("123456")).status(1).build();
            userMapper.insert(buyer);
        }
    }

    @Test
    @DisplayName("CAS 防超卖：10 人并发抢同一商品，仅 1 人成功")
    void concurrentPurchaseCasGuardsAgainstOversell() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        Thread[] threads = new Thread[BUYER_COUNT];

        for (int i = 0; i < BUYER_COUNT; i++) {
            final int buyerIndex = i;
            threads[i] = new Thread(() -> {
                try {
                    Long buyerId = userMapper.getByStudentId(
                            "buyer" + String.format("%03d", buyerIndex)).getUserId();
                    BaseContext.setCurrentId(buyerId);
                    OrdersDTO dto = new OrdersDTO();
                    dto.setItemId(itemId);
                    latch.await();
                    orderService.create(dto);
                    successCount.incrementAndGet();
                } catch (OrderBusinessException e) {
                    failCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    BaseContext.removeCurrentId();
                }
            });
            threads[i].start();
        }

        Thread.sleep(500);
        latch.countDown();
        for (Thread t : threads) t.join(5000);

        assertEquals(1, successCount.get(), "仅 1 人购买成功");
        assertEquals(BUYER_COUNT - 1, failCount.get(), "其余全部失败");
        assertEquals(2, itemMapper.getById(itemId).getStatus(), "商品状态应为已售出(2)");
    }
}
