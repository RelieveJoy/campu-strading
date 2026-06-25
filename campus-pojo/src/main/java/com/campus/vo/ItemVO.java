package com.campus.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "商品列表项")
public class ItemVO implements Serializable {

    @Schema(description = "商品ID")
    private Long itemId;

    @Schema(description = "商品标题")
    private String title;

    @Schema(description = "售价")
    private BigDecimal price;

    @Schema(description = "原价")
    private BigDecimal originalPrice;

    @Schema(description = "商品图片URL")
    private String imageUrl;

    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "卖家姓名")
    private String sellerName;

    @Schema(description = "浏览量")
    private Integer viewCount;

    @Schema(description = "成色")
    private Integer itemCondition;

    @Schema(description = "状态：1在售 0下架 2已售出")
    private Integer status;

    @Schema(description = "商品平均评分")
    private BigDecimal averageRating;

    @Schema(description = "有效评分数")
    private Integer reviewCount;

    @Schema(description = "发布时间")
    private LocalDateTime createTime;
}
