package com.myshop.util;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    // 从 application.properties 中注入密钥
    @Value("${application.jwt.secret-key}")
    private String secretKey;

    // 从 application.properties 中注入过期时间
    @Value("${application.jwt.expiration}")
    private long jwtExpiration;

    /**
     * 从JWT中提取用户名
     * @param token JWT字符串
     * @return 用户名
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * 为指定用户生成JWT
     * @param userDetails 用户信息
     * @return JWT字符串
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * 验证JWT是否对特定用户有效
     * @param token JWT字符串
     * @param userDetails 用户信息
     * @return 如果有效则返回true
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        // 检查JWT中的用户名是否与UserDetails中的用户名匹配，并且JWT没有过期
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    // --- 私有辅助方法 ---

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername()) // 将用户名作为 subject
                .setIssuedAt(new Date(System.currentTimeMillis())) // 设置签发时间
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration)) // 设置过期时间
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // 使用HS256算法和密钥进行签名
                .compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}