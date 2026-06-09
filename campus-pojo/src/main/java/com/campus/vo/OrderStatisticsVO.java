package com.campus.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "数据统计返回")
public class OrderStatisticsVO implements Serializable {

    @Schema(description = "名称（分类名/日期等）")
    private String name;

    @Schema(description = "数量")
    private Long value;

}
