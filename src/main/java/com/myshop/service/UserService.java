//主厨
package com.myshop.service; // <-- 已修改

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.myshop.mapper.UserMapper; // <-- 已修改 import 路径
import com.myshop.model.User;       // <-- 已修改 import 路径

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User getUserByUsername(String username) {
        return userMapper.findByUsername(username);
    }
}