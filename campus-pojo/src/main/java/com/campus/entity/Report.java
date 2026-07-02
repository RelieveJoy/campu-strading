package com.campus.entity;

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
public class Report implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String TARGET_ITEM = "item";
    public static final String TARGET_USER = "user";
    public static final Integer STATUS_PENDING = 1;
    public static final Integer STATUS_RESOLVED = 2;

    private Long reportId;
    private Long reporterId;
    private String targetType;   // item / user
    private Long targetId;
    private String reason;
    private String description;
    private Integer status;      // 1待处理 2已处理
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
