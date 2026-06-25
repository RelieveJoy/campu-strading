package com.campus.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "商品评分摘要")
public class ItemRatingVO implements Serializable {

    @Schema(description = "商品ID")
    private Long itemId;

    @Schema(description = "平均评分，无有效评分时为0")
    private BigDecimal averageRating;

    @Schema(description = "有效评分数")
    private Integer reviewCount;
}
