package com.campus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "用户分页查询条件")
public class UserPageQueryDTO implements Serializable {

    @Schema(description = "页码")
    private Integer page;

    @Schema(description = "每页条数")
    private Integer pageSize;

    @Schema(description = "用户姓名")
    private String name;

}
