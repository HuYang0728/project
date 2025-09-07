//主厨
package com.myshop.service; // <-- 已修改

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.myshop.mapper.UserMapper;
import com.myshop.model.User;
import com.myshop.util.JwtUtil;

@Service
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder; // 声明加密器
    private final JwtUtil jwtUtil;

    // 使用构造函数注入，这是 Spring 推荐的最佳实践
    @Autowired
    public UserService(UserMapper userMapper, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil; // 注入 JwtUtil
    }

    public User getUserByUsername(String username) {
        return userMapper.findByUsername(username);
    }
    
    // 新增：用户注册的核心方法
    public User registerUser(User user) {
        // 1. 检查用户名是否已存在
        if (userMapper.findByUsername(user.getUsername()) != null) {
            // 在真实项目中，这里应该抛出一个自定义的异常，比如 UsernameAlreadyExistsException
            throw new RuntimeException("用户名已存在!");
        }

         // 2. --- 核心修改在这里 ---
        // 从新增的、专门接收明文的 password 字段获取密码
        String hashedPassword = passwordEncoder.encode(user.getPassword());
    
        // 将加密后的结果，设置到专门存储哈希值的 passwordHash 字段
        user.setPasswordHash(hashedPassword); 

        // 3. 将带有加密密码的用户信息存入数据库
        userMapper.insertUser(user);
    
        // 4. 返回插入后的用户信息
        return user;
    }
    public String loginUser(String username, String rawPassword) {
        // 1. 根据用户名查找用户
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 2. 验证密码
        // passwordEncoder.matches 会自动处理加密比对
        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 3. 密码验证成功，生成 JWT
        return jwtUtil.generateToken(user);
    }
}