package com.campus.service.impl;

import com.campus.constant.MessageConstant;
import com.campus.constant.StatusConstant;
import com.campus.context.BaseContext;
import com.campus.dto.UserLoginDTO;
import com.campus.dto.UserRegisterDTO;
import com.campus.dto.PasswordEditDTO;
import com.campus.entity.User;
import com.campus.exception.AccountLockedException;
import com.campus.exception.AccountNotFoundException;
import com.campus.exception.PasswordErrorException;
import com.campus.exception.PasswordEditFailedException;
import com.campus.mapper.UserMapper;
import com.campus.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 用户登录
     *
     * @param userLoginDTO
     * @return
     */
    public User login(UserLoginDTO userLoginDTO) {
        String studentId = userLoginDTO.getStudentId();
        String password = userLoginDTO.getPassword();

        //1、根据学号查询数据库中的数据
        User user = userMapper.getByStudentId(studentId);

        //2、处理各种异常情况（学号不存在、密码不对、账号被锁定）
        if (user == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        //对前端传过来的密码进行md5加密处理
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        if (!password.equals(user.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (user.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return user;
    }

    /**
     * 用户注册
     * @param userRegisterDTO
     */
    @Override
    public void register(UserRegisterDTO userRegisterDTO) {
        //检查学号是否已存在
        User existUser = userMapper.getByStudentId(userRegisterDTO.getStudentId());
        if (existUser != null) {
            throw new AccountNotFoundException("该学号已注册");
        }

        User user = new User();
        BeanUtils.copyProperties(userRegisterDTO, user);
        user.setStatus(StatusConstant.ENABLE);
        user.setPassword(DigestUtils.md5DigestAsHex(userRegisterDTO.getPassword().getBytes()));

        userMapper.insert(user);
    }

    /**
     * 根据id查询用户
     * @param userId
     * @return
     */
    @Override
    public User getById(Long userId) {
        User user = userMapper.getById(userId);
        user.setPassword("****");
        return user;
    }

    /**
     * 修改用户信息
     * @param user
     */
    public void update(User user) {
        userMapper.update(user);
    }

    /**
     * 修改密码
     * @param passwordEditDTO
     */
    public void editPassword(PasswordEditDTO passwordEditDTO) {
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);
        String oldPassword = DigestUtils.md5DigestAsHex(passwordEditDTO.getOldPassword().getBytes());
        if (!oldPassword.equals(user.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }
        user.setPassword(DigestUtils.md5DigestAsHex(passwordEditDTO.getNewPassword().getBytes()));
        userMapper.update(user);
    }

}
