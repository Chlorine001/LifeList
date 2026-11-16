package com.lifelist.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lifelist.common.BizException;
import com.lifelist.common.JwtUtil;
import com.lifelist.common.UserContext;
import com.lifelist.dto.LoginRequest;
import com.lifelist.dto.LoginResponse;
import com.lifelist.dto.RegisterRequest;
import com.lifelist.entity.User;
import com.lifelist.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 用户/登录服务
 * 核心：注册用 BCrypt 加密密码，登录校验密码后发 JWT
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;

    /** BCrypt 密码加密器 */
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /** 注册 */
    public LoginResponse register(RegisterRequest req) {
        // 用户名唯一性校验
        Long exists = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getUsername, req.getUsername()));
        if (exists != null && exists > 0) {
            throw new BizException("用户名已存在");
        }

        User user = new User();
        user.setUsername(req.getUsername());
        // 关键：密码用 BCrypt 加密后再存，数据库里永远是密文
        user.setPassword(encoder.encode(req.getPassword()));
        user.setNickname(req.getNickname() == null ? req.getUsername() : req.getNickname());
        userMapper.insert(user);

        return buildLoginResponse(user);
    }

    /** 登录 */
    public LoginResponse login(LoginRequest req) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, req.getUsername()));
        if (user == null) {
            throw new BizException("用户名或密码错误");
        }
        // 关键：用 encoder.matches 比对明文和密文，而不是直接比字符串
        if (!encoder.matches(req.getPassword(), user.getPassword())) {
            throw new BizException("用户名或密码错误");
        }
        return buildLoginResponse(user);
    }

    /** 修改昵称 */
    public LoginResponse updateNickname(String nickname) {
        if (nickname == null || nickname.trim().isEmpty()) {
            throw new BizException("昵称不能为空");
        }
        User user = userMapper.selectById(UserContext.get());
        if (user == null) {
            throw new BizException("用户不存在");
        }
        user.setNickname(nickname.trim());
        userMapper.updateById(user);
        return buildLoginResponse(user);
    }

    private LoginResponse buildLoginResponse(User user) {
        LoginResponse resp = new LoginResponse();
        resp.setToken(jwtUtil.generate(user.getId(), user.getUsername()));
        resp.setUserId(user.getId());
        resp.setUsername(user.getUsername());
        resp.setNickname(user.getNickname());
        resp.setAvatar(user.getAvatar());
        return resp;
    }
}
