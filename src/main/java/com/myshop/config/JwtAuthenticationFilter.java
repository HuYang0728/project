// a. 从请求头 Authorization 中获取 JWT 字符串。
// b. 使用您的 JwtUtil 来验证这个 JWT 是否有效。
// c. 如果有效，就从 JWT 中解析出用户信息（比如用户名），然后告诉 Spring Security：“这位用户已经认证通过了”。
// d. 如果无效，就拦截请求。
package com.myshop.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.myshop.util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * JWT认证过滤器
 * 这是保护API端点的核心组件。
 * 它会在每个请求到达Controller之前运行一次。
 */
@Component // 将这个过滤器注册为Spring容器中的一个Bean
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    // 我们需要一个能根据用户名加载用户信息的服务
    private final UserDetailsService userDetailsService;

    @Autowired
    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // 1. 从请求头中获取 "Authorization"
        final String authHeader = request.getHeader("Authorization");

        // 2. 如果请求头为空，或不是以 "Bearer " 开头，则直接放行，让后续的过滤器处理
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. 提取 JWT (去掉 "Bearer " 前缀)
        final String jwt = authHeader.substring(7);
        final String username;

        try {
            // 4. 从JWT中解析出用户名
            username = jwtUtil.extractUsername(jwt);
        } catch (Exception e) {
            // 如果JWT解析失败，直接放行，后续的Security过滤器会因为没有认证信息而拦截请求
            filterChain.doFilter(request, response);
            return;
        }


        // 5. 检查用户名是否存在，并且当前安全上下文中没有已认证的用户
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 6. 根据用户名加载用户信息
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 7. 验证JWT是否有效
            if (jwtUtil.isTokenValid(jwt, userDetails)) {
                // 8. 如果JWT有效，创建一个认证令牌
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // 对于JWT认证，我们不需要凭证（密码）
                        userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                // 9. 将认证令牌设置到安全上下文中，表示该用户已通过认证
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        // 10. 将请求传递给过滤器链中的下一个过滤器
        filterChain.doFilter(request, response);
    }
}
