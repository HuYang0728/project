//配菜师
package com.myshop.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.myshop.model.User; 

@Mapper
public interface UserMapper {
    User findByUsername(String username);
    int insertUser(User user);
}
