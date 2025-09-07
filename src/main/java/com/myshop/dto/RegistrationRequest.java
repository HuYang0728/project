package com.myshop.dto;

import lombok.Data;

@Data
public class RegistrationRequest {
    private String username;
    private String email;
    private String password; // <-- 在这里也使用 password
}