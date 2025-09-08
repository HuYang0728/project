//菜谱
package com.myshop.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // 导入 HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration // 告诉 Spring 这是一个配置类
@EnableWebSecurity // 启用 Spring Security 的 Web 安全支持
@EnableMethodSecurity // 启用方法级别的安全性注解支持
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Autowired
    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean

    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                 // 1. 公开访问的接口
                .requestMatchers("/api/users/register", "/api/users/login").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/products", "/api/products/**").permitAll()
            
                // 2. ★★★ 核心修改：为购物车接口添加规则 ★★★
                // 所有 /api/cart/ 开头的路径，都必须是已认证（登录）的用户才能访问
                .requestMatchers("/api/cart/**").authenticated()
            
                // 3. ★★★ 修复安全漏洞：保护管理员接口 ★★★
                // 所有 /api/admin/ 开头的路径，都必须是拥有 "ADMIN" 角色的用户才能访问
                // 注意: hasRole("ADMIN") 会自动匹配数据库中的 "ROLE_ADMIN"
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
            
                // 4. 其他任何未匹配到的请求，为了安全起见，可以选择拒绝或要求认证
                // 这里我们保持原样，要求认证
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
