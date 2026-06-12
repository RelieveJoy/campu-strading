package com.campus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "用户注册时传递的数据模型")
public class UserRegisterDTO implements Serializable {

    @NotBlank(message = "学号不能为空")
    @Schema(description = "学号")
    private String studentId;

    @NotBlank(message = "姓名不能为空")
    @Size(max = 20, message = "姓名最长20个字符")
    @Schema(description = "姓名")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度为6-20位")
    @Schema(description = "密码")
    private String password;

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号")
    private String phone;

}
