package com.lifelist.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具类
 * 负责「生成 token」和「解析 token」
 *
 * 登录流程：
 *   1. 用户登录成功 -> 生成一个 token 发给前端
 *   2. 前端后续请求都带上 token（放 Authorization 头）
 *   3. 后端拦截器解析 token，拿到 userId，判断是谁在操作
 */
@Component
public class JwtUtil {

    /** 密钥，从配置读取（生产环境要改成足够长的随机串） */
    @Value("${lifelist.jwt.secret}")
    private String secret;

    /** token 有效期（毫秒），默认 7 天 */
    @Value("${lifelist.jwt.expire:604800000}")
    private long expire;

    private SecretKey key() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /** 生成 token，把 userId 和 username 塞进 claims */
    public String generate(Long userId, String username) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("username", username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expire))
                .signWith(key())
                .compact();
    }

    /** 解析 token，返回 claims；失败抛异常 */
    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /** 从 token 里取 userId */
    public Long getUserId(String token) {
        return Long.parseLong(parse(token).getSubject());
    }
}
