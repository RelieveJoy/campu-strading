package com.campus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Schema(description = "商品新增/编辑时传递的数据模型")
public class ItemDTO implements Serializable {

    @NotBlank(message = "商品标题不能为空")
    @Size(max = 100, message = "商品标题最长100个字符")
    @Schema(description = "商品标题")
    private String title;

    @Size(max = 500, message = "商品描述最长500个字符")
    @Schema(description = "商品描述")
    private String description;

    @NotNull(message = "售价不能为空")
    @DecimalMin(value = "0.01", message = "售价必须大于0")
    @Schema(description = "售价")
    private BigDecimal price;

    @DecimalMin(value = "0", message = "原价不能为负数")
    @Schema(description = "原价")
    private BigDecimal originalPrice;

    @NotNull(message = "分类不能为空")
    @Positive(message = "请选择有效分类")
    @Schema(description = "分类ID")
    private Long categoryId;

    @NotBlank(message = "商品图片不能为空")
    @Schema(description = "商品图片URL")
    private String imageUrl;

    @Schema(description = "状态：1在售 0下架 2已售出")
    private Integer status;

}
