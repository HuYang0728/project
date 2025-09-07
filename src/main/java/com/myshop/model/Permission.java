package com.myshop.model;

import lombok.Data;

/**
 * 权限实体类，对应 permissions 表
 * 代表一个具体的操作，例如 "product:create"
 */
@Data
public class Permission {

    private Long id;
    private String name;

}