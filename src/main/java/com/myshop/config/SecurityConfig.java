//菜谱
package com.myshop.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // 导入 HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration // 告诉 Spring 这是一个配置类
@EnableWebSecurity // 启用 Spring Security 的 Web 安全支持
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Autowired
    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. 关闭 CSRF 防护
            .csrf(csrf -> csrf.disable())
            
            // 2. 配置URL授权规则
            .authorizeHttpRequests(authz -> authz
                // 对注册和登录接口，允许所有用户访问
                .requestMatchers("/api/users/register", "/api/users/login").permitAll()
                
                // --- *** 新增的规则 *** ---
                // 允许任何人通过 GET 方法访问商品列表和商品详情
                .requestMatchers(HttpMethod.GET, "/api/products", "/api/products/**").permitAll()
                
                .requestMatchers("/api/admin/**").permitAll() 
                // 其他所有请求都必须经过认证
                .anyRequest().authenticated()
            )
            
            // 3. 配置 Session 管理策略为 STATELESS (无状态)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // 4. 将我们自定义的JWT认证过滤器，添加到Spring Security的过滤器链中
            // 它会在标准的用户名密码认证过滤器之前运行
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
