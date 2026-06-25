package com.campus.service;

import com.campus.context.BaseContext;
import com.campus.dto.ItemDTO;
import com.campus.mapper.UserMapper;
import com.campus.vo.ItemVO;
import com.campus.vo.MessageVO;
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
class MessageServiceTest {

    @Autowired private MessageService messageService;
    @Autowired private UserService userService;
    @Autowired private ItemService itemService;
    @Autowired private UserMapper userMapper;

    private Long itemId;
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
        itemDTO.setTitle("聊天测试商品");
        itemDTO.setDescription("test");
        itemDTO.setPrice(new BigDecimal("100"));
        itemDTO.setCategoryId(1L);
        itemDTO.setImageUrl("https://example.com/img.jpg");
        itemService.add(itemDTO);
        var items = itemService.getByUserId(sellerId, null, 1, 10).getRecords();
        itemId = ((ItemVO) items.get(0)).getItemId();
    }

    @Test
    @DisplayName("买家对商品发消息给卖家")
    void buyerMessagesSellerAboutItem() {
        BaseContext.setCurrentId(buyerId);
        MessageVO vo = messageService.send(buyerId, itemId, sellerId, "你好，这个书还在吗？");
        assertNotNull(vo.getMessageId());
        assertEquals(buyerId, vo.getSenderId());
        assertEquals(itemId, vo.getItemId());
    }

    @Test
    @DisplayName("买卖双方聊天记录包含双方消息")
    void historyContainsBothSides() {
        BaseContext.setCurrentId(buyerId);
        messageService.send(buyerId, itemId, sellerId, "买家：书还在吗");

        BaseContext.setCurrentId(sellerId);
        messageService.send(sellerId, itemId, buyerId, "卖家：还在的");

        BaseContext.setCurrentId(buyerId);
        List<MessageVO> history = messageService.getHistory(itemId, buyerId, sellerId);
        assertEquals(2, history.size());
        assertEquals("买家：书还在吗", history.get(0).getContent());
        assertEquals("卖家：还在的", history.get(1).getContent());
    }

    @Test
    @DisplayName("多个买家问同一个商品，卖家各自回复不串")
    void multipleBuyersSellerRepliesToCorrectOne() {
        // 注册另外两个买家
        userService.register(buildUserDTO("buyer02", "买家二"));
        userService.register(buildUserDTO("buyer03", "买家三"));
        Long buyer2Id = userMapper.getByStudentId("buyer02").getUserId();
        Long buyer3Id = userMapper.getByStudentId("buyer03").getUserId();

        // 三个买家各自发消息
        BaseContext.setCurrentId(buyerId);  // buyer01
        messageService.send(buyerId, itemId, sellerId, "买家1：书还在吗");

        BaseContext.setCurrentId(buyer2Id);
        messageService.send(buyer2Id, itemId, sellerId, "买家2：能便宜点吗");

        BaseContext.setCurrentId(buyer3Id);
        messageService.send(buyer3Id, itemId, sellerId, "买家3：我直接拍了");

        // 卖家先回复买家2
        messageService.send(sellerId, itemId, buyer2Id, "卖家回复2：可以便宜10块");
        // 再回复买家3
        messageService.send(sellerId, itemId, buyer3Id, "卖家回复3：好的等你拍");

        // 验证：买家2看到的历史包含2条（自己的+卖家的），不包含其他买家的
        BaseContext.setCurrentId(buyer2Id);
        List<MessageVO> histBuyer2 = messageService.getHistory(itemId, buyer2Id, sellerId);
        assertEquals(2, histBuyer2.size());
        assertEquals("买家2：能便宜点吗", histBuyer2.get(0).getContent());
        assertEquals("卖家回复2：可以便宜10块", histBuyer2.get(1).getContent());

        // 验证：买家1的历史只有自己的1条（卖家没回复他，且不包含买家2/3的消息）
        BaseContext.setCurrentId(buyerId);
        List<MessageVO> histBuyer1 = messageService.getHistory(itemId, buyerId, sellerId);
        assertEquals(1, histBuyer1.size());
        assertEquals("买家1：书还在吗", histBuyer1.get(0).getContent());

        // 验证：买家3的历史也是2条，不混入别人的
        BaseContext.setCurrentId(buyer3Id);
        List<MessageVO> histBuyer3 = messageService.getHistory(itemId, buyer3Id, sellerId);
        assertEquals(2, histBuyer3.size());
    }

    @Test
    @DisplayName("标记已读")
    void markReadWorks() {
        BaseContext.setCurrentId(buyerId);
        messageService.send(buyerId, itemId, sellerId, "你好");

        messageService.markRead(sellerId, itemId);
        List<MessageVO> history = messageService.getHistory(itemId, buyerId, sellerId);
        assertEquals(1, history.get(0).getIsRead());
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
