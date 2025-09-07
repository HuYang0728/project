//菜谱
package com.myshop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // 告诉 Spring 这是一个配置类
@EnableWebSecurity // 启用 Spring Security 的 Web 安全支持
public class SecurityConfig {

    @Bean // 将这个方法的返回值注册为 Spring 容器中的一个 Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    // --- 这是新增的核心配置 ---
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. 关闭 CSRF 防护 (对于无状态API是推荐做法)
            .csrf(csrf -> csrf.disable())
            
            // 2. 禁用默认的登录表单
            .formLogin(form -> form.disable())
            
            // 3. 禁用 HTTP Basic 认证
            .httpBasic(basic -> basic.disable())
            
            // 4. 配置 Session 管理策略为 STATELESS (无状态)
            // 这对于 RESTful API 至关重要，服务器不会创建或使用 HttpSession
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // 5. 配置 URL 的授权规则 (这部分你已经写对了)
            .authorizeHttpRequests(authz -> authz
                // 对 /api/users/register 和 /api/users/login 这两个路径的请求，允许所有用户访问
                .requestMatchers("/api/users/register", "/api/users/login").permitAll()
                // 除了上面放行的路径外，其他所有请求都必须经过认证
                .anyRequest().authenticated()
            );

        return http.build();
    }
}