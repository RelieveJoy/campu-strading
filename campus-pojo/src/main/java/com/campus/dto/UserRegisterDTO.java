package com.campus.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(description = "用户注册时传递的数据模型")
public class UserRegisterDTO implements Serializable {

    @ApiModelProperty("学号")
    private String studentId;

    @ApiModelProperty("姓名")
    private String username;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("手机号")
    private String phone;

}
