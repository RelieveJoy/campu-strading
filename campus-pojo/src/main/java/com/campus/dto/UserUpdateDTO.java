package com.campus.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(description = "用户修改个人信息时传递的数据模型")
public class UserUpdateDTO implements Serializable {

    @ApiModelProperty("昵称")
    private String username;

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("头像URL")
    private String avatar;
}
