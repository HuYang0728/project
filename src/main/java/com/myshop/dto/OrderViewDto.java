// 文件路径: com/myshop/dto/OrderViewDto.java
package com.myshop.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.myshop.model.Order;
import com.myshop.model.OrderStatus;

import lombok.Data;

/**
 * @description 用于向前端展示订单详情的数据传输对象
 */
@Data
public class OrderViewDto {
    private String orderNo;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private String shippingAddress;
    private LocalDateTime createdAt;
    private List<OrderItemDto> items;

    // 内部静态 DTO，用于表示订单中的单个商品
    @Data
    public static class OrderItemDto {
        private Long productId;
        private String productName;
        private String productImageUrl;
        private Integer quantity;
        private BigDecimal priceAtPurchase;
    }

    /**
     * @description 一个非常方便的静态工厂方法，用于将领域模型 Order 转换为 DTO
     * @param order 数据库查询出的 Order 实体对象
     * @return 转换后的 DTO 对象
     */
    public static OrderViewDto from(Order order) {
        OrderViewDto dto = new OrderViewDto();
        dto.setOrderNo(order.getOrderNo());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setCreatedAt(order.getCreatedAt());
        
        // 使用 Java Stream API 将 OrderItem 列表转换为 OrderItemDto 列表
        List<OrderItemDto> itemDtos = order.getOrderItems().stream().map(orderItem -> {
            OrderItemDto itemDto = new OrderItemDto();
            itemDto.setProductId(orderItem.getProductId());
            itemDto.setProductName(orderItem.getProductName());
            itemDto.setProductImageUrl(orderItem.getProductImageUrl());
            itemDto.setQuantity(orderItem.getQuantity());
            itemDto.setPriceAtPurchase(orderItem.getPriceAtPurchase());
            return itemDto;
        }).collect(Collectors.toList());
        
        dto.setItems(itemDtos);
        
        return dto;
    }
}