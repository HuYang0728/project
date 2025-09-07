//食材
package com.myshop.model;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * 用户实体类，对应 users 表
 * 一个用户拥有多个角色
 * 同时实现了 Spring Security 的 UserDetails 接口
 */
@Data
public class User implements UserDetails { // <-- 确保实现了 UserDetails

    private Long id;
    private String username;
    private String email;

    @JsonIgnore // 在序列化为 JSON 时，忽略这个字段
    private String passwordHash;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // --- 新增的 password 字段，用于接收注册时的明文密码 ---
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String plainPassword; // 在你的注册逻辑中会用到

    // --- ★★★ 核心修改 1: 添加角色集合 ★★★ ---
    /**
     * 用户拥有的角色集合
     */
    private Set<Role> roles;

    // ------------------------------------------------------------------
    // --- ★★★ 核心修改 2: 实现 UserDetails 接口的核心方法 ★★★ ---
    // ------------------------------------------------------------------

    /**
     * 获取用户的权限集合。
     * 这是 Spring Security 用于权限判断的核心方法。
     * 我们需要在这里将用户的角色和具体权限都转换成 GrantedAuthority 对象。
     */
    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        // 遍历用户的所有角色
        for (Role role : this.roles) {
            // 1. 添加角色本身作为权限，必须以 "ROLE_" 开头
            authorities.add(new SimpleGrantedAuthority(role.getName()));
            // 2. 添加该角色下的所有具体权限
            for (Permission permission : role.getPermissions()) {
                authorities.add(new SimpleGrantedAuthority(permission.getName()));
            }
        }
        return authorities;
    }

    /**
     * UserDetails 接口要求实现的方法：返回存储的、已加密的密码
     */
    @Override
    @JsonIgnore
    public String getPassword() {
        return this.passwordHash;
    }

    /**
     * UserDetails 接口要求实现的方法：用户名
     */
    @Override
    public String getUsername() {
        return this.username;
    }

    // --- 以下是 UserDetails 接口的其他方法，为了简单起见，我们都返回 true ---

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }
}