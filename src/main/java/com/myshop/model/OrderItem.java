package com.myshop.model;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class OrderItem {
    private Long id;
    private Long orderId;
    private Long productId;
    private String productName;
    private String productImageUrl;
    private Integer quantity;
    private BigDecimal priceAtPurchase;
}