//配菜师
package com.myshop.mapper;

import com.myshop.model.Role;
import com.myshop.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param; // 导入 Param 注解
import java.util.List;
import java.util.Set;

@Mapper
public interface UserMapper {

    // --- 已有方法 ---
    User findByUsername(String username);
    void insertUser(User user);

    // --- ★★★ 新增方法 ★★★ ---

    User findById(Long id); // 根据ID查询用户，方便后续使用

    // 查询系统所有角色
    List<Role> findAllRoles();

    // 查询指定用户拥有的所有角色
    Set<Role> findRolesByUserId(Long userId);

    // 为用户添加一个角色
    void addRoleToUser(@Param("userId") Long userId, @Param("roleId") Long roleId);

    // 从用户移除一个角色
    void removeRoleFromUser(@Param("userId") Long userId, @Param("roleId") Long roleId);
}
