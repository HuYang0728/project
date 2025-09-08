package com.myshop.model;

public enum OrderStatus {
    PENDING_PAYMENT, // 待付款
    PAID,            // 已付款/待发货
    SHIPPED,         // 已发货
    DELIVERED,       // 已送达
    COMPLETED,       // 已完成
    CANCELED         // 已取消
}