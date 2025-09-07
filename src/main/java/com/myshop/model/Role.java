package com.myshop.model;

import java.util.Set;

import lombok.Data;

/**
 * 角色实体类，对应 roles 表
 * 一个角色包含多个权限
 */
@Data
public class Role {

    private Long id;
    private String name;

    /**
     * 角色拥有的权限集合
     * 使用 Set 是为了防止重复的权限
     * Mybatis 会在查询时填充这个集合
     */
    private Set<Permission> permissions;
}