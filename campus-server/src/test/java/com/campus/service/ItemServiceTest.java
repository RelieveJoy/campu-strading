package com.campus.service;

import com.campus.context.BaseContext;
import com.campus.dto.ItemDTO;
import com.campus.entity.Item;
import com.campus.entity.User;
import com.campus.mapper.ItemMapper;
import com.campus.mapper.UserMapper;
import com.campus.dto.ItemPageQueryDTO;
import com.campus.result.PageResult;
import com.campus.vo.ItemDetailVO;
import com.campus.vo.ItemVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Sql(scripts = "/schema.sql")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ItemServiceTest {

    @Autowired private ItemService itemService;
    @Autowired private ItemMapper itemMapper;
    @Autowired private UserMapper userMapper;
    @Autowired private RedisTemplate redisTemplate;
    @Autowired private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        redisTemplate.delete("cache:home:items");
        User seller = User.builder()
                .studentId("seller01").username("卖家")
                .password(passwordEncoder.encode("123456")).status(1).build();
        userMapper.insert(seller);
        // 自增 ID 不会回写，需要查出来
        Long sellerId = userMapper.getByStudentId("seller01").getUserId();
        BaseContext.setCurrentId(sellerId);
    }

    @Test
    @DisplayName("发布商品成功")
    void publishSuccess() {
        ItemDTO dto = buildItemDTO("闲置吉他", new BigDecimal("300.00"));
        assertDoesNotThrow(() -> itemService.add(dto));
        List<ItemVO> homeItems = itemService.getHomeItems();
        assertFalse(homeItems.isEmpty());
        assertTrue(homeItems.stream().anyMatch(i -> "闲置吉他".equals(i.getTitle())));
    }

    @Test
    @DisplayName("编辑自己的商品成功")
    void editOwnItemSuccess() {
        itemService.add(buildItemDTO("旧标题", new BigDecimal("100.00")));
        Long itemId = itemService.getHomeItems().get(0).getItemId();

        ItemDTO dto = buildItemDTO("新标题", new BigDecimal("150.00"));
        itemService.update(itemId, dto);
        ItemDetailVO detail = itemService.getById(itemId);
        assertEquals("新标题", detail.getTitle());
    }

    @Test
    @DisplayName("不能编辑他人的商品")
    void editOtherItemThrows() {
        itemService.add(buildItemDTO("旧标题", new BigDecimal("100.00")));
        Long itemId = itemService.getHomeItems().get(0).getItemId();

        User other = User.builder()
                .studentId("other001").username("别人")
                .password(passwordEncoder.encode("123456")).status(1).build();
        userMapper.insert(other);
        BaseContext.setCurrentId(other.getUserId());

        ItemDTO dto = buildItemDTO("篡改", new BigDecimal("1.00"));
        assertThrows(Exception.class, () -> itemService.update(itemId, dto));
    }

    @Test
    @DisplayName("下架后状态变为0")
    void deleteSetsStatusToZero() {
        itemService.add(buildItemDTO("待下架", new BigDecimal("50.00")));
        Long itemId = itemService.getHomeItems().get(0).getItemId();
        itemService.delete(itemId);
        assertEquals(0, itemMapper.getById(itemId).getStatus());
    }

    @Test
    @DisplayName("下架后重新上架恢复状态为1")
    void relistRestoresStatus() {
        itemService.add(buildItemDTO("待下架", new BigDecimal("50.00")));
        Long itemId = itemService.getHomeItems().get(0).getItemId();
        itemService.delete(itemId);
        itemService.relist(itemId);
        assertEquals(1, itemMapper.getById(itemId).getStatus());
    }

    @Test
    @DisplayName("空库首页返回空列表并缓存")
    void emptyHomeReturnsEmptyList() {
        redisTemplate.delete("cache:home:items");
        List<ItemVO> homeItems = itemService.getHomeItems();
        assertNotNull(homeItems);
        assertTrue(homeItems.isEmpty());
        List<ItemVO> cached = (List<ItemVO>) redisTemplate.opsForValue().get("cache:home:items");
        assertNotNull(cached);
        assertTrue(cached.isEmpty());
    }

    @Test
    @DisplayName("分页查询商品")
    void pageQueryReturnsPage() {
        for (int i = 0; i < 3; i++) {
            itemService.add(buildItemDTO("商品" + i, new BigDecimal("10")));
        }
        ItemPageQueryDTO dto = new ItemPageQueryDTO();
        dto.setPage(1);
        dto.setPageSize(10);
        PageResult result = itemService.pageQuery(dto);
        assertTrue(result.getTotal() >= 3);
    }

    @Test
    @DisplayName("根据ID查询商品详情")
    void getByIdReturnsDetail() {
        itemService.add(buildItemDTO("详情商品", new BigDecimal("200")));
        Long itemId = itemService.getHomeItems().get(0).getItemId();
        ItemDetailVO detail = itemService.getById(itemId);
        assertEquals("详情商品", detail.getTitle());
    }

    @Test
    @DisplayName("根据用户ID查询发布的商品")
    void getByUserIdReturnsUserItems() {
        itemService.add(buildItemDTO("我的商品", new BigDecimal("30")));
        Long sellerId = userMapper.getByStudentId("seller01").getUserId();
        List<ItemVO> items = itemService.getByUserId(sellerId);
        assertFalse(items.isEmpty());
        assertTrue(items.stream().anyMatch(i -> "我的商品".equals(i.getTitle())));
    }

    private ItemDTO buildItemDTO(String title, BigDecimal price) {
        ItemDTO dto = new ItemDTO();
        dto.setTitle(title);
        dto.setDescription("测试");
        dto.setPrice(price);
        dto.setOriginalPrice(price.add(new BigDecimal("50")));
        dto.setCategoryId(1L);
        dto.setImageUrl("https://example.com/img.jpg");
        return dto;
    }
}
