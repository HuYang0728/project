package com.myshop.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Order {
    private Long id;
    private String orderNo;
    private Long userId;
    private BigDecimal totalAmount;
    private OrderStatus status; // 使用枚举类型
    private String shippingAddress;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 一个订单包含多个订单项
    private List<OrderItem> orderItems = new ArrayList<>(); 
}