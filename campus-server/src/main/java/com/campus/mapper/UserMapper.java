package com.campus.mapper;

import com.campus.annotation.AutoFill;
import com.campus.entity.User;
import com.campus.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    /**
     * 根据学号查询用户
     * @param studentId
     * @return
     */
    @Select("select * from user where student_id = #{studentId}")
    User getByStudentId(String studentId);

    /**
     * 新增用户
     * @param user
     */
    @Insert("insert into user(student_id, username, password, phone, avatar, bio, gender, birthday, status, create_time, update_time, create_user, update_user) " +
            "values " +
            "(#{studentId}, #{username}, #{password}, #{phone}, #{avatar}, #{bio}, #{gender}, #{birthday}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    @AutoFill(value = OperationType.INSERT)
    void insert(User user);

    /**
     * 根据id查询用户
     * @param userId
     * @return
     */
    @Select("select * from user where user_id = #{userId}")
    User getById(Long userId);

    /**
     * 根据主键动态修改
     * @param user
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(User user);

    /**
     * 根据 GitHub 账号ID 查询用户（OAuth 登录用）
     */
    @Select("select * from user where oauth_github_id = #{githubId}")
    User getByGithubId(Long githubId);

    /**
     * 新增 OAuth 用户（GitHub 登录自动建号）
     */
    @Insert("insert into user(student_id, username, password, avatar, oauth_github_id, status, create_time, update_time, create_user, update_user)" +
          "values (#{studentId}, #{username}, #{password}, #{avatar},#{oauthGithubId}, #{status}, #{createTime}, #{updateTime}, #{createUser},#{updateUser})")
    @AutoFill(value = OperationType.INSERT)
    @Options(useGeneratedKeys = true, keyProperty = "userId")
    void insertOauthUser(User user);
}
