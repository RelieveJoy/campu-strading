package com.campus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "用户登录时传递的数据模型")
public class UserLoginDTO implements Serializable {

    @NotBlank(message = "学号不能为空")
    @Size(max = 50, message = "学号最长50个字符")
    @Schema(description = "学号")
    private String studentId;

    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码")
    private String password;

}
