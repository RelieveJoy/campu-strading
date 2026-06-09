package com.campus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "用户注册时传递的数据模型")
public class UserRegisterDTO implements Serializable {

    @Schema(description = "学号")
    private String studentId;

    @Schema(description = "姓名")
    private String username;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "手机号")
    private String phone;

}
