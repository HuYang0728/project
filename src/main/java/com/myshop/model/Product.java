package com.myshop.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Product {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String imageUrl; // 对应数据库的 image_url
    private String status;
    private LocalDateTime createdAt; // 对应数据库的 created_at
    private LocalDateTime updatedAt; // 对应数据库的 updated_at
}