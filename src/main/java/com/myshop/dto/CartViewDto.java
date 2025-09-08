//用于表示整个购物车
package com.myshop.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class CartViewDto {
    private List<CartItemViewDto> items;
    private BigDecimal totalPrice; // 购物车总价

    public CartViewDto(List<CartItemViewDto> items) {
        this.items = items;
        this.totalPrice = items.stream()
                               .map(CartItemViewDto::getSubtotal)
                               .reduce(BigDecimal.ZERO, BigDecimal::add); // 自动计算总价
    }
}