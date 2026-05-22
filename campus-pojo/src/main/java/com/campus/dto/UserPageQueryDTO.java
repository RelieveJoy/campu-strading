package com.campus.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(description = "用户分页查询条件")
public class UserPageQueryDTO implements Serializable {

    @ApiModelProperty("页码")
    private Integer page;

    @ApiModelProperty("每页条数")
    private Integer pageSize;

    @ApiModelProperty("用户姓名")
    private String name;

}
