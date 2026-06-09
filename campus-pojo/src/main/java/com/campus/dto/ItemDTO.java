package com.campus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Schema(description = "商品新增/编辑时传递的数据模型")
public class ItemDTO implements Serializable {

    @Schema(description = "商品标题")
    private String title;

    @Schema(description = "商品描述")
    private String description;

    @Schema(description = "售价")
    private BigDecimal price;

    @Schema(description = "原价")
    private BigDecimal originalPrice;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "商品图片URL")
    private String imageUrl;

    @Schema(description = "状态：1在售 0下架 2已售出")
    private Integer status;

}
