package com.campus.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "数据统计返回")
public class OrderStatisticsVO implements Serializable {

    @ApiModelProperty("名称（分类名/日期等）")
    private String name;

    @ApiModelProperty("数量")
    private Long value;

}
