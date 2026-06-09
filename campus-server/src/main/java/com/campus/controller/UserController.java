package com.campus.controller;

import com.campus.constant.JwtClaimsConstant;
import com.campus.constant.MessageConstant;
import com.campus.context.BaseContext;
import com.campus.dto.UserLoginDTO;
import com.campus.dto.UserRegisterDTO;
import com.campus.dto.UserUpdateDTO;
import com.campus.dto.PasswordEditDTO;
import com.campus.exception.BaseException;
import com.campus.entity.User;
import com.campus.properties.JwtProperties;
import com.campus.result.Result;
import com.campus.service.ItemService;
import com.campus.service.UserService;
import com.campus.utils.JwtUtil;
import com.campus.vo.ItemVO;
import com.campus.vo.UserLoginVO;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 用户管理
 */
@RestController
@RequestMapping("/api/users")
@Slf4j
@Tag(name = "用户相关接口")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 登录
     *
     * @param userLoginDTO
     * @return
     */
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("用户登录：{}", userLoginDTO);

        User user = userService.login(userLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getUserId());
        claims.put(JwtClaimsConstant.STUDENT_ID, user.getStudentId());
        claims.put(JwtClaimsConstant.USERNAME, user.getUsername());
        String token = JwtUtil.createJWT(
                jwtProperties.getUserSecretKey(),
                jwtProperties.getUserTtl(),
                claims);

        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getUserId())
                .studentId(user.getStudentId())
                .username(user.getUsername())
                .token(token)
                .build();

        return Result.success(userLoginVO);
    }

    /**
     * 注册
     *
     * @param userRegisterDTO
     * @return
     */
    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<String> register(@RequestBody UserRegisterDTO userRegisterDTO) {
        log.info("用户注册：{}", userRegisterDTO);
        userService.register(userRegisterDTO);
        return Result.success();
    }

    /**
     * 根据id查询用户信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据id查询用户信息")
    public Result<User> getById(@PathVariable Long id) {
        User user = userService.getById(id);
        return Result.success(user);
    }

    /**
     * 修改用户信息
     * @param id
     * @param user
     * @return
     */
    @Operation(summary = "修改用户信息")
    @PutMapping("/{id}")
    public Result update(@PathVariable Long id, @RequestBody UserUpdateDTO userUpdateDTO) {
        if (!id.equals(BaseContext.getCurrentId())) {
            throw new BaseException(MessageConstant.PERMISSION_DENIED);
        }
        log.info("修改用户信息：{}", userUpdateDTO);
        User user = new User();
        BeanUtils.copyProperties(userUpdateDTO, user);
        user.setUserId(id);
        userService.update(user);
        return Result.success();
    }

    /**
     * 修改密码
     * @param passwordEditDTO
     * @return
     */
    @Operation(summary = "修改密码")
    @PutMapping("/password")
    public Result editPassword(@RequestBody PasswordEditDTO passwordEditDTO) {
        userService.editPassword(passwordEditDTO);
        return Result.success();
    }

    /**
     * 用户登出
     * @param request
     * @return
     */
    @Operation(summary = "查看用户发布的商品")
    @GetMapping("/{id}/items")
    public Result<List<ItemVO>> getUserItems(@PathVariable Long id) {
        log.info("查询用户 {} 发布的商品", id);
        List<ItemVO> items = itemService.getByUserId(id);
        return Result.success(items);
    }

    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request) {
        String token = request.getHeader(jwtProperties.getUserTokenName());
        String blacklistKey = "blacklist:token:" + token;

        // 幂等：Token 已在黑名单中，直接返回成功
        if (Boolean.TRUE.equals(redisTemplate.hasKey(blacklistKey))) {
            log.info("用户重复登出，token已在黑名单");
            return Result.success();
        }

        Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), token);
        long remainingTtl = claims.getExpiration().getTime() - System.currentTimeMillis();
        if (remainingTtl > 0) {
            redisTemplate.opsForValue()
                    .set(blacklistKey, "1", remainingTtl, TimeUnit.MILLISECONDS);
        }
        BaseContext.removeCurrentId();
        log.info("用户已登出，token加入黑名单");
        return Result.success();
    }
}
