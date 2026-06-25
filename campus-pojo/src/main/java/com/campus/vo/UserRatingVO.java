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
@Schema(description = "用户综合评分")
public class UserRatingVO implements Serializable {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "综合评分，无有效评分时为0")
    private BigDecimal overallRating;

    @Schema(description = "有评分的商品数量")
    private Integer ratedItemCount;
}
