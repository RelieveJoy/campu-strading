package com.campus.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel(description = "商品新增/编辑时传递的数据模型")
public class ItemDTO implements Serializable {

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

    @ApiModelProperty("商品图片URL")
    private String imageUrl;

    @ApiModelProperty("状态：1在售 0下架 2已售出")
    private Integer status;

}
