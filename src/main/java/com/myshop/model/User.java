//食材
package com.myshop.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data // Lombok 注解: 自动生成 Getter, Setter, toString, equals, hashCode 等方法
public class User {

    private Long id;
    private String username;
    private String passwordHash; // 注意: Java中用驼峰命名法, 对应数据库的 password_hash
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

}
