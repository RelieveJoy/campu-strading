package com.campus.service.impl;

import com.campus.constant.JwtClaimsConstant;
import com.campus.entity.User;
import com.campus.exception.LoginFailedException;
import com.campus.mapper.UserMapper;
import com.campus.properties.JwtProperties;
import com.campus.properties.OAuthProperties;
import com.campus.service.OAuthService;
import com.campus.utils.JwtUtil;
import com.campus.vo.UserLoginVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuthServiceImpl implements OAuthService {

    private final OAuthProperties oAuthProperties;
    private final UserMapper userMapper;
    private final JwtProperties jwtProperties;
    private final StringRedisTemplate stringRedisTemplate;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    @Transactional
    public UserLoginVO githubLogin(String code, String state) {
        // ① 校验 state（防 CSRF）：Redis 里有且只能消费一次
        Boolean consumed = stringRedisTemplate.delete("oauth_state:" + state);
        if (!Boolean.TRUE.equals(consumed)) {
            throw new LoginFailedException("state 校验失败，登录请求已过期或非法");
        }

        // ② code 换 access_token（POST 到 GitHub，带 client_secret）
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", oAuthProperties.getClientId());
        body.add("client_secret", oAuthProperties.getClientSecret());
        body.add("code", code);
        body.add("redirect_uri", oAuthProperties.getRedirectUri());
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        Map<String, Object> tokenResp = restTemplate.postForObject(
                oAuthProperties.getTokenUrl(), request, Map.class);
        String accessToken = tokenResp != null ? (String) tokenResp.get("access_token") : null;
        if (accessToken == null) {
            throw new LoginFailedException("获取 GitHub access_token 失败");
        }

        // ③ 带 token 调 GitHub API 拿用户信息
        HttpHeaders headers2 = new HttpHeaders();
        headers2.setBearerAuth(accessToken);
        HttpEntity<Void> request2 = new HttpEntity<>(headers2);
        Map<String, Object> githubUser = restTemplate.exchange(
                oAuthProperties.getUserUrl(), HttpMethod.GET, request2, Map.class).getBody();
        if (githubUser == null || githubUser.get("id") == null) {
            throw new LoginFailedException("获取 GitHub 用户信息失败");
        }

        Long githubId = Long.valueOf(String.valueOf(githubUser.get("id")));
        String username = (String) githubUser.get("login");

        // ④ 查库：有则登录，无则自动建号
        User user = userMapper.getByGithubId(githubId);
        if (user == null) {
            user = User.builder()
                    .studentId("github_" + githubId)                // 合成学号，满足 NOT NULL UNIQUE
                    .username(username != null ? username : "github_" + githubId)
                    .password(UUID.randomUUID().toString())          // 随机密码，OAuth 用户不走密码登录
                    .avatar((String) githubUser.get("avatar_url"))
                    .oauthGithubId(githubId)
                    .status(1)
                    .build();
            userMapper.insertOauthUser(user);
        }

        // ⑤ 签发 JWT（复用登录逻辑）
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getUserId());
        claims.put(JwtClaimsConstant.STUDENT_ID, user.getStudentId());
        claims.put(JwtClaimsConstant.USERNAME, user.getUsername());
        String token = JwtUtil.createJWT(
                jwtProperties.getUserSecretKey(),
                jwtProperties.getUserTtl(),
                claims);

        return UserLoginVO.builder()
                .id(user.getUserId())
                .studentId(user.getStudentId())
                .username(user.getUsername())
                .token(token)
                .build();
    }
}
