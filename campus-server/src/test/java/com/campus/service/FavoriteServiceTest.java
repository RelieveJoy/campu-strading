package com.campus.service;

import com.campus.context.BaseContext;
import com.campus.dto.FavoriteDTO;
import com.campus.dto.ItemDTO;
import com.campus.entity.User;
import com.campus.exception.ItemBusinessException;
import com.campus.mapper.UserMapper;
import com.campus.vo.FavoriteVO;
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
class FavoriteServiceTest {

    @Autowired private FavoriteService favoriteService;
    @Autowired private ItemService itemService;
    @Autowired private UserService userService;
    @Autowired private UserMapper userMapper;
    @Autowired private PasswordEncoder passwordEncoder;

    private Long itemId;

    @BeforeEach
    void setUp() {
        // 注册卖家
        userService.register(buildUserDTO("seller99", "卖家"));
        Long sellerId = userMapper.getByStudentId("seller99").getUserId();
        BaseContext.setCurrentId(sellerId);

        // 发商品
        ItemDTO itemDTO = new ItemDTO();
        itemDTO.setTitle("收藏测试商品");
        itemDTO.setDescription("test");
        itemDTO.setPrice(new BigDecimal("100"));
        itemDTO.setCategoryId(1L);
        itemDTO.setImageUrl("https://example.com/img.jpg");
        itemService.add(itemDTO);
        itemId = itemService.getByUserId(sellerId).get(0).getItemId();

        // 切回买家身份
        userService.register(buildUserDTO("fan001", "收藏者"));
        Long fanId = userMapper.getByStudentId("fan001").getUserId();
        BaseContext.setCurrentId(fanId);
    }

    @Test
    @DisplayName("收藏商品成功")
    void addFavoriteSuccess() {
        FavoriteDTO dto = new FavoriteDTO();
        dto.setItemId(itemId);
        assertDoesNotThrow(() -> favoriteService.add(dto));
        assertTrue(favoriteService.check(itemId));
    }

    @Test
    @DisplayName("重复收藏同一个商品应失败")
    void addDuplicateFavoriteThrows() {
        FavoriteDTO dto = new FavoriteDTO();
        dto.setItemId(itemId);
        favoriteService.add(dto);
        assertThrows(ItemBusinessException.class, () -> favoriteService.add(dto));
    }

    @Test
    @DisplayName("取消收藏成功")
    void deleteFavoriteSuccess() {
        FavoriteDTO dto = new FavoriteDTO();
        dto.setItemId(itemId);
        favoriteService.add(dto);
        List<FavoriteVO> list = favoriteService.list();
        assertEquals(1, list.size());
        favoriteService.delete(list.get(0).getFavoriteId());
        assertEquals(0, favoriteService.list().size());
    }

    @Test
    @DisplayName("检查未收藏商品返回 false")
    void checkNotFavoritedReturnsFalse() {
        assertFalse(favoriteService.check(itemId));
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
