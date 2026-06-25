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
public class Review implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long reviewId;
    private Long orderId;
    private Long itemId;
    private Long reviewerId;
    private Long targetId;
    private Integer rating;   // 0未评 1~5
    private String content;
    private LocalDateTime createTime;
}
