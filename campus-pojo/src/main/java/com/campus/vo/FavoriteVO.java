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
@Schema(description = "收藏列表项")
public class FavoriteVO implements Serializable {

    @Schema(description = "收藏ID")
    private Long favoriteId;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "商品ID")
    private Long itemId;

    @Schema(description = "商品标题")
    private String itemTitle;

    @Schema(description = "商品图片URL")
    private String itemImageUrl;

    @Schema(description = "商品价格")
    private BigDecimal itemPrice;

    @Schema(description = "收藏时间")
    private LocalDateTime createTime;
}
