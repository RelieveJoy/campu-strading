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
public class NotificationVO implements Serializable {

    private Long notificationId;
    private Long initiatorId;
    private String initiatorName;
    private String type;
    private Long orderId;
    private String itemTitle;
    private String content;
    private Integer isRead;
    private LocalDateTime createTime;
}
