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
@ApiModel(description = "商品详情（含卖家信息）")
public class ItemDetailVO implements Serializable {

    @ApiModelProperty("商品ID")
    private Long itemId;

    @ApiModelProperty("商品标题")
    private String title;

    @ApiModelProperty("商品描述")
    private String description;

    @ApiModelProperty("售价")
    private BigDecimal price;

    @ApiModelProperty("原价")
    private BigDecimal originalPrice;

    @ApiModelProperty("分类ID")
    private Long categoryId;

    @ApiModelProperty("分类名称")
    private String categoryName;

    @ApiModelProperty("商品图片URL")
    private String imageUrl;

    @ApiModelProperty("浏览量")
    private Integer viewCount;

    @ApiModelProperty("状态：1在售 0下架 2已售出")
    private Integer status;

    @ApiModelProperty("卖家ID")
    private Long sellerId;

    @ApiModelProperty("卖家姓名")
    private String sellerName;

    @ApiModelProperty("卖家手机号")
    private String sellerPhone;

    @ApiModelProperty("发布时间")
    private LocalDateTime createTime;
}
