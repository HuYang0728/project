package com.myshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor // Lombok: 生成一个包含所有参数的构造函数
public class LoginResponse {
    private String token;
}