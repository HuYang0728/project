//服务员
package com.myshop.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myshop.dto.LoginRequest;
import com.myshop.dto.LoginResponse;
import com.myshop.dto.RegistrationRequest;
import com.myshop.dto.UserDto;
import com.myshop.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@RequestBody RegistrationRequest request) {
        // 直接将 DTO 传递给 Service，让 Service 处理转换和注册逻辑
        UserDto registeredUserDto = userService.registerUser(request);
        return new ResponseEntity<>(registeredUserDto, HttpStatus.CREATED);
    }
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        String token = userService.loginUser(loginRequest.getUsername(), loginRequest.getPassword());
        return ResponseEntity.ok(new LoginResponse(token));
    }
    
    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(Principal principal) {
        // 直接从 Service 获取 DTO
        UserDto userDto = userService.getUserDtoByUsername(principal.getName());
        return ResponseEntity.ok(userDto);
    }
}
