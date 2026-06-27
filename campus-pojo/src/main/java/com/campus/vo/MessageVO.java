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
public class MessageVO implements Serializable {

    private Long messageId;
    private Long itemId;
    private String sourceType;
    private Long senderId;
    private Long receiverId;
    private String senderName;
    private String content;
    private Integer isRead;
    private LocalDateTime createTime;
}
