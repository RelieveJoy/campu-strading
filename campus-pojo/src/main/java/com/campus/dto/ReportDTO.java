package com.campus.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReportDTO {

    @NotBlank(message = "举报类型不能为空")
    private String targetType;   // item / user

    @NotNull(message = "举报目标ID不能为空")
    private Long targetId;

    @NotBlank(message = "举报原因不能为空")
    private String reason;

    private String description;
}
