package com.myshop.model;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class CartItem {

    private Long id;
    private Long userId;
    private Long productId;
    private Integer quantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // (可选) 为了方便在Service层返回给前端，可以临时存放商品信息
    // 这个字段在数据库中不存在，需要特别处理
    // private Product product; 
}