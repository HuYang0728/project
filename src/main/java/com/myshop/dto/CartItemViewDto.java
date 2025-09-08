//用于表示购物车中的单个商品项
package com.myshop.dto;

import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CartItemViewDto {
    private Long productId;
    private String productName;
    private String imageUrl;
    private BigDecimal price; // 商品单价
    private Integer quantity;
    private BigDecimal subtotal; // 小计 (单价 * 数量)

    public CartItemViewDto(Long productId, String productName, String imageUrl, BigDecimal price, Integer quantity) {
        this.productId = productId;
        this.productName = productName;
        this.imageUrl = imageUrl;
        this.price = price;
        this.quantity = quantity;
        this.subtotal = price.multiply(BigDecimal.valueOf(quantity)); // 自动计算小计
    }
}