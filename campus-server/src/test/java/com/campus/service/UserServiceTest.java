package com.campus.service;

import com.campus.constant.MessageConstant;
import com.campus.context.BaseContext;
import com.campus.dto.PasswordEditDTO;
import com.campus.dto.UserLoginDTO;
import com.campus.dto.UserRegisterDTO;
import com.campus.entity.User;
import com.campus.exception.AccountNotFoundException;
import com.campus.exception.PasswordErrorException;
import com.campus.exception.StudentIdAlreadyExistsException;
import com.campus.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Sql(scripts = "/schema.sql")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class UserServiceTest {

    @Autowired private UserService userService;
    @Autowired private UserMapper userMapper;
    @Autowired private PasswordEncoder passwordEncoder;

    private static final String STUDENT_ID = "2024001";
    private static final String PASSWORD = "abc123";

    @Nested
    @DisplayName("注册")
    class Register {

        @Test
        @DisplayName("注册成功")
        void registerSuccess() {
            UserRegisterDTO dto = buildRegisterDTO(STUDENT_ID, "张三", PASSWORD, "13800001111");
            assertDoesNotThrow(() -> userService.register(dto));
            User user = userMapper.getByStudentId(STUDENT_ID);
            assertNotNull(user);
            assertEquals("张三", user.getUsername());
            assertTrue(passwordEncoder.matches(PASSWORD, user.getPassword()));
        }

        @Test
        @DisplayName("学号重复注册失败")
        void registerDuplicateStudentIdThrows() {
            userService.register(buildRegisterDTO(STUDENT_ID, "张三", PASSWORD, "13800001111"));
            StudentIdAlreadyExistsException ex = assertThrows(
                    StudentIdAlreadyExistsException.class,
                    () -> userService.register(buildRegisterDTO(STUDENT_ID, "李四", "other", "13900002222")));
            assertEquals(MessageConstant.STUDENT_ID_ALREADY_EXISTS, ex.getMessage());
        }
    }

    @Nested
    @DisplayName("登录")
    class Login {

        @BeforeEach
        void registerUser() {
            userService.register(buildRegisterDTO(STUDENT_ID, "张三", PASSWORD, "13800001111"));
        }

        @Test
        @DisplayName("登录成功")
        void loginSuccess() {
            UserLoginDTO dto = buildLoginDTO(STUDENT_ID, PASSWORD);
            User user = assertDoesNotThrow(() -> userService.login(dto));
            assertNotNull(user);
            assertEquals(STUDENT_ID, user.getStudentId());
        }

        @Test
        @DisplayName("学号不存在")
        void loginAccountNotFound() {
            UserLoginDTO dto = buildLoginDTO("nonexist", PASSWORD);
            AccountNotFoundException ex = assertThrows(
                    AccountNotFoundException.class,
                    () -> userService.login(dto));
            assertEquals(MessageConstant.ACCOUNT_NOT_FOUND, ex.getMessage());
        }

        @Test
        @DisplayName("密码错误")
        void loginWrongPassword() {
            UserLoginDTO dto = buildLoginDTO(STUDENT_ID, "wrong123");
            PasswordErrorException ex = assertThrows(
                    PasswordErrorException.class,
                    () -> userService.login(dto));
            assertEquals(MessageConstant.PASSWORD_ERROR, ex.getMessage());
        }
    }

    @Test
    @DisplayName("根据ID查询用户，密码已脱敏")
    void getByIdMasksPassword() {
        userService.register(buildRegisterDTO(STUDENT_ID, "张三", PASSWORD, "13800001111"));
        User user = userMapper.getByStudentId(STUDENT_ID);
        User result = userService.getById(user.getUserId());
        assertEquals("****", result.getPassword());
    }

    @Test
    @DisplayName("修改密码成功")
    void editPasswordSuccess() {
        userService.register(buildRegisterDTO(STUDENT_ID, "张三", PASSWORD, "13800001111"));
        User user = userMapper.getByStudentId(STUDENT_ID);
        BaseContext.setCurrentId(user.getUserId());

        PasswordEditDTO dto = new PasswordEditDTO();
        dto.setOldPassword(PASSWORD);
        dto.setNewPassword("newpwd123");
        assertDoesNotThrow(() -> userService.editPassword(dto));
    }

    @Test
    @DisplayName("修改密码时旧密码错误则失败")
    void editPasswordWrongOldPasswordThrows() {
        userService.register(buildRegisterDTO(STUDENT_ID, "张三", PASSWORD, "13800001111"));
        User user = userMapper.getByStudentId(STUDENT_ID);
        BaseContext.setCurrentId(user.getUserId());

        PasswordEditDTO dto = new PasswordEditDTO();
        dto.setOldPassword("wrongpwd");
        dto.setNewPassword("newpwd123");
        assertThrows(PasswordErrorException.class, () -> userService.editPassword(dto));
    }

    private UserRegisterDTO buildRegisterDTO(String sid, String name, String pwd, String phone) {
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setStudentId(sid);
        dto.setUsername(name);
        dto.setPassword(pwd);
        dto.setPhone(phone);
        return dto;
    }

    private UserLoginDTO buildLoginDTO(String sid, String pwd) {
        UserLoginDTO dto = new UserLoginDTO();
        dto.setStudentId(sid);
        dto.setPassword(pwd);
        return dto;
    }
}
