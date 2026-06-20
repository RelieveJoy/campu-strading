package com.campus.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 参数校验测试：验证 @Valid 在 Controller 层生效。
 */
@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/schema.sql")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ValidationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Test
    @DisplayName("登录参数为空时返回校验错误")
    void loginWithEmptyBodyReturnsValidationError() throws Exception {
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.msg").value(
                        org.hamcrest.Matchers.containsString("学号不能为空")));
    }

    @Test
    @DisplayName("注册手机号格式错误返回校验提示")
    void registerWithInvalidPhoneReturnsValidationError() throws Exception {
        Map<String, String> body = Map.of(
                "studentId", "2024001",
                "username", "测试",
                "password", "123456",
                "phone", "not-a-phone"
        );
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.msg").value(
                        org.hamcrest.Matchers.containsString("手机号格式不正确")));
    }

    @Test
    @DisplayName("商品必填字段为空时返回多条校验错误")
    void publishItemWithEmptyFieldsReturnsMultipleErrors() throws Exception {
        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.msg").value(
                        org.hamcrest.Matchers.containsString("商品标题不能为空")))
                .andExpect(jsonPath("$.msg").value(
                        org.hamcrest.Matchers.containsString("售价不能为空")));
    }
}
