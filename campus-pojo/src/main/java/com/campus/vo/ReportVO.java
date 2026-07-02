package com.campus.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "举报列表项")
public class ReportVO implements Serializable {

    @Schema(description = "举报ID")
    private Long reportId;

    @Schema(description = "举报人ID")
    private Long reporterId;

    @Schema(description = "举报人姓名")
    private String reporterName;

    @Schema(description = "举报类型 item=商品 user=用户")
    private String targetType;

    @Schema(description = "被举报目标ID")
    private Long targetId;

    @Schema(description = "举报原因")
    private String reason;

    @Schema(description = "详细描述")
    private String description;

    @Schema(description = "状态 1待处理 2已处理")
    private Integer status;

    @Schema(description = "举报时间")
    private LocalDateTime createTime;
}
