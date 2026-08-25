package com.campus.service;

import com.campus.vo.UserLoginVO;

public interface OAuthService {

    /** GitHub OAuth 登录：校验 state → code换token → 取用户 → 建号/绑定 → 签发JWT */
    UserLoginVO githubLogin(String code, String state);
}
