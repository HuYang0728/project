//服务员
package com.myshop.controller; // <-- 已修改

import com.myshop.dto.LoginRequest; // 导入 DTO
import com.myshop.dto.LoginResponse; // 导入 DTO
import com.myshop.model.User;
import com.myshop.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{username}")
    public User getUser(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }

    // 新增：处理用户注册的 API 接口
    // @PostMapping 表示这个接口只接受 HTTP POST 请求
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        // @RequestBody 告诉 Spring Boot 从请求的 JSON Body 中解析数据并填充到 User 对象里
        User registeredUser = userService.registerUser(user);
        // 使用 ResponseEntity 可以更好地控制 HTTP 状态码，201 Created 表示资源创建成功
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        String token = userService.loginUser(loginRequest.getUsername(), loginRequest.getPassword());
        return ResponseEntity.ok(new LoginResponse(token));
    }    
}