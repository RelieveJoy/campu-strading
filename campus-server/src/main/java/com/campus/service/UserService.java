package com.campus.service;

import com.campus.dto.UserLoginDTO;
import com.campus.dto.UserRegisterDTO;
import com.campus.dto.PasswordEditDTO;
import com.campus.entity.User;

public interface UserService {

    /**
     * 用户登录
     * @param userLoginDTO
     * @return
     */
    User login(UserLoginDTO userLoginDTO);

    /**
     * 用户注册
     * @param userRegisterDTO
     */
    void register(UserRegisterDTO userRegisterDTO);

    /**
     * 根据id查询用户信息
     * @param userId
     * @return
     */
    User getById(Long userId);

    /**
     * 修改用户信息
     * @param user
     */
    void update(User user);

    /**
     * 修改密码
     * @param passwordEditDTO
     */
    void editPassword(PasswordEditDTO passwordEditDTO);
}
