package com.campus.controller;

import com.campus.properties.OAuthProperties;
import com.campus.result.Result;
import com.campus.service.OAuthService;
import com.campus.vo.UserLoginVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/api/oauth/github")
@RequiredArgsConstructor
public class OAuthController {

    private final OAuthProperties oAuthProperties;
    private final StringRedisTemplate stringRedisTemplate;
    private final OAuthService oAuthService;

    /** 生成 GitHub 授权链接，前端点击后浏览器跳转到它 */
    @GetMapping("/login")
    public Result<String> login() {
        //生成随机 state，存 Redis 5 分钟（防 CSRF，回调时校验）
        String state = UUID.randomUUID().toString();
        stringRedisTemplate.opsForValue()
                .set("oauth_state:" + state, "1", Duration.ofMinutes(5));

        //拼 GitHub 授权 URL
        String redirectUri = URLEncoder.encode(oAuthProperties.getRedirectUri(), StandardCharsets.UTF_8);
        String url = oAuthProperties.getAuthorizeUrl()
                + "?client_id=" + oAuthProperties.getClientId()
                + "&redirect_uri=" + redirectUri
                + "&scope=read:user"
                + "&state=" + state;

        //返回给前端，前端 window.location.href = url 即可跳转
        return Result.success(url);
    }

    /** GitHub 授权回调：code 换 token → 建号/登录 → 302 跳回前端带 JWT */
    @GetMapping("/callback")
    public void callback(@RequestParam String code, @RequestParam String state,
                         HttpServletResponse response) throws IOException {
        try {
            UserLoginVO vo = oAuthService.githubLogin(code, state);
            // 302 跳回前端登录成功页（前端是 hash 路由，所以 URL 要带 #/）
            // 把 token + 用户信息放进 URL，前端回调页拼出完整的登录态
            response.sendRedirect("http://localhost:5173/#/oauth/callback?token=" + vo.getToken()
                    + "&id=" + vo.getId()
                    + "&studentId=" + URLEncoder.encode(vo.getStudentId(), StandardCharsets.UTF_8)
                    + "&username=" + URLEncoder.encode(vo.getUsername(), StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error("GitHub 登录回调失败", e);
            // 跳回前端并带上错误信息
            response.sendRedirect("http://localhost:5173/#/oauth/callback?error="
                    + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8));
        }
    }
}
