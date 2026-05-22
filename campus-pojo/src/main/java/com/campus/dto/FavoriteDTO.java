package com.campus.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(description = "收藏操作时传递的数据模型")
public class FavoriteDTO implements Serializable {

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("商品ID")
    private Long itemId;

}
