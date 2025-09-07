//配菜师
package com.myshop.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.myshop.model.User; 

@Mapper
public interface UserMapper {//工作指令清单
    User findByUsername(String username);
    int insertUser(User user);
}
