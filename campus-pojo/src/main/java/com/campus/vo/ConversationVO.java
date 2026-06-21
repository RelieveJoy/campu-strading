package com.campus.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationVO implements Serializable {

    private Long itemId;
    private String itemTitle;
    private String itemImage;       // 商品首图
    private Long otherUserId;       // 对方的用户ID
    private String otherUserName;   // 对方用户名
    private String lastContent;     // 最后一条消息内容
    private LocalDateTime lastTime; // 最后一条消息时间
    private Integer unreadCount;    // 未读消息数
}
