package com.yaozhidao.controller;

import com.yaozhidao.common.Result;
import com.yaozhidao.dto.request.LoginRequest;
import com.yaozhidao.dto.request.SendCodeRequest;
import com.yaozhidao.dto.response.LoginResponse;
import com.yaozhidao.dto.response.SendCodeResponse;
import com.yaozhidao.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 认证：发送短信验证码 / 手机号验证码登录 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /** POST /api/v1/auth/send-code 发送短信验证码（60秒一次，5分钟有效） */
    @PostMapping("/send-code")
    public Result<SendCodeResponse> sendCode(@Valid @RequestBody SendCodeRequest req) {
        return Result.success("验证码已发送", authService.sendCode(req));
    }

    /** POST /api/v1/auth/login 手机号验证码登录（新用户自动注册） */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        return Result.success("登录成功", authService.login(req));
    }
}
