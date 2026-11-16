package com.lifelist.controller;

import com.lifelist.common.R;
import com.lifelist.dto.LoginRequest;
import com.lifelist.dto.LoginResponse;
import com.lifelist.dto.RegisterRequest;
import com.lifelist.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 认证接口：注册、登录、修改昵称
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public R<LoginResponse> register(@Valid @RequestBody RegisterRequest req) {
        return R.ok(authService.register(req));
    }

    @PostMapping("/login")
    public R<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        return R.ok(authService.login(req));
    }

    /** 修改昵称（需要登录） */
    @PutMapping("/profile")
    public R<LoginResponse> updateNickname(@RequestBody Map<String, String> body) {
        return R.ok(authService.updateNickname(body.get("nickname")));
    }
}
