package com.lifelist.dto;

import lombok.Data;

/**
 * 登录成功返回：token + 用户信息
 */
@Data
public class LoginResponse {

    private String token;
    private Long userId;
    private String username;
    private String nickname;
    private String avatar;
}
