package com.myshop.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import com.myshop.model.User;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component // 把它也声明为一个 Bean，方便注入
public class JwtUtil {

    // 秘钥，这是签名的关键。在真实项目中，应该更复杂，并从配置文件中读取
    private final String SECRET_KEY = "mySecretKeyForMyShopProjectThisShouldBeVeryLongAndSecure";

    // Token 过期时间，这里设置为 10 小时
    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 10;

    /**
     * 根据用户信息生成 Token
     */
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        // 你可以在 claims 中放入任何想包含在 Token 里的信息
        claims.put("userId", user.getId());
        claims.put("email", user.getEmail());

        return Jwts.builder()
                .setClaims(claims) // 设置自定义声明
                .setSubject(user.getUsername()) // 设置主题，通常是用户名
                .setIssuedAt(new Date(System.currentTimeMillis())) // 设置签发时间
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 设置过期时间
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY) // 设置签名算法和秘钥
                .compact();
    }

    // ... 未来可以在这里添加验证 Token 的方法 ...
}