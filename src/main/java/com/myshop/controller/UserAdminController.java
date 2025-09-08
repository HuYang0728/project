package com.myshop.controller;

import com.myshop.dto.AssignRoleRequest;
import com.myshop.model.Role;
import com.myshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/admin") // API 根路径
public class UserAdminController {

    private final UserService userService;

    @Autowired
    public UserAdminController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 获取系统所有可用角色列表
     */
    @GetMapping("/roles")
    @PreAuthorize("hasAuthority('user:manage')")
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(userService.getAllRoles());
    }

    /**
     * 获取指定用户的所有角色
     */
    @GetMapping("/users/{userId}/roles")
    @PreAuthorize("hasAuthority('user:manage')")
    public ResponseEntity<Set<Role>> getRolesForUser(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getRolesByUserId(userId));
    }
    
    /**
     * 为指定用户分配一个角色
     */
    @PostMapping("/users/{userId}/roles")
    @PreAuthorize("hasAuthority('user:manage')")
    public ResponseEntity<Void> assignRoleToUser(@PathVariable Long userId, @RequestBody AssignRoleRequest request) {
        userService.assignRoleToUser(userId, request.getRoleId());
        return ResponseEntity.ok().build();
    }

    /**
     * 移除指定用户的指定角色
     */
    @DeleteMapping("/users/{userId}/roles/{roleId}")
    @PreAuthorize("hasAuthority('user:manage')")
    public ResponseEntity<Void> removeRoleFromUser(@PathVariable Long userId, @PathVariable Long roleId) {
        userService.removeRoleFromUser(userId, roleId);
        return ResponseEntity.noContent().build();
    }
}