//主厨
package com.myshop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.myshop.dto.RegistrationRequest; // 导入 DTO
import com.myshop.dto.UserDto; // 导入 DTO
import com.myshop.exception.UsernameAlreadyExistsException;
import com.myshop.mapper.UserMapper;
import com.myshop.model.User;
import com.myshop.util.JwtUtil;

@Service
public class UserService implements UserDetailsService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserService(UserMapper userMapper, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("找不到用户名为: " + username + " 的用户");
        }
        return user;
    }

    // --- 新增的方法：根据用户名直接返回 UserDto ---
    public UserDto getUserDtoByUsername(String username) {
        User user = this.getUserByUsername(username);
        // 在 Service 层完成 User -> UserDto 的转换
        return new UserDto(user.getId(), user.getUsername(), user.getEmail());
    }
    
    public User getUserByUsername(String username) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("找不到用户名为: " + username + " 的用户");
        }
        return user;
    }
    
    // --- 修改的方法：接收 RegistrationRequest，返回 UserDto ---
    public UserDto registerUser(RegistrationRequest request) {
        if (userMapper.findByUsername(request.getUsername()) != null) {
            throw new UsernameAlreadyExistsException("用户名 '" + request.getUsername() + "' 已存在!");
        }

        // 1. 在 Service 层将 DTO 转换为实体
        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        
        // 2. 加密密码并设置
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        newUser.setPasswordHash(hashedPassword); 
        
        // 3. 插入数据库
        userMapper.insertUser(newUser);

        // 4. 将新创建的 User 实体转换为 DTO 并返回
        return new UserDto(newUser.getId(), newUser.getUsername(), newUser.getEmail());
    }
    
    public String loginUser(String username, String rawPassword) {
        UserDetails userDetails = this.loadUserByUsername(username);

        if (!passwordEncoder.matches(rawPassword, userDetails.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        return jwtUtil.generateToken(userDetails);
    }
}