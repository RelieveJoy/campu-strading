package com.campus.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(description = "收藏列表项")
public class FavoriteVO implements Serializable {

    @ApiModelProperty("收藏ID")
    private Long favoriteId;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("商品ID")
    private Long itemId;

    @ApiModelProperty("商品标题")
    private String itemTitle;

    @ApiModelProperty("商品图片URL")
    private String itemImageUrl;

    @ApiModelProperty("商品价格")
    private BigDecimal itemPrice;

    @ApiModelProperty("收藏时间")
    private LocalDateTime createTime;
}
