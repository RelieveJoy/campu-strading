package com.campus.vo;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "评价展示")
public class ReviewVO implements Serializable {

    @Schema(description = "评价ID")
    private Long reviewId;

    @Schema(description = "关联订单ID")
    private Long orderId;

    @Schema(description = "评分人姓名")
    private String reviewerName;

    @Schema(description = "商品名称")
    private String itemTitle;

    @Schema(description = "评分 0未评 1~5")
    private Integer rating;

    @Schema(description = "评价内容")
    private String content;

    @Schema(description = "评价时间")
    private LocalDateTime createTime;
}
