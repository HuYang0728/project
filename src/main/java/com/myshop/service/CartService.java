package com.myshop.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.myshop.dto.CartItemViewDto;
import com.myshop.dto.CartViewDto;
import com.myshop.mapper.CartMapper;
import com.myshop.mapper.ProductMapper;
import com.myshop.model.CartItem;
import com.myshop.model.Product;

@Service
public class CartService {

    private final CartMapper cartMapper;
    private final ProductMapper productMapper;

    @Autowired
    public CartService(CartMapper cartMapper, ProductMapper productMapper) {
        this.cartMapper = cartMapper;
        this.productMapper = productMapper;
    }

    /**
     * 添加商品到购物车
     */
    @Transactional // 原子性
    public void addItemToCart(Long userId, Long productId, Integer quantity) {
        // 1. 检查商品是否存在
        Product product = productMapper.findById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        
        // 2. 检查库存是否充足
        if (product.getStock() < quantity) {
            throw new RuntimeException("商品库存不足");
        }

        // 3. 检查用户的购物车是否已有此商品
        CartItem existingItem = cartMapper.findByUserAndProduct(userId, productId);

        if (existingItem != null) {
            // 4a. 如果已存在，则更新数量
            int newQuantity = existingItem.getQuantity() + quantity;
            existingItem.setQuantity(newQuantity);
            cartMapper.updateItemQuantity(existingItem);
        } else {
            // 4b. 如果不存在，则插入新纪录
            CartItem newItem = new CartItem();
            newItem.setUserId(userId);
            newItem.setProductId(productId);
            newItem.setQuantity(quantity);
            cartMapper.insertItem(newItem);
        }
    }

    /**
     * 获取用户的购物车视图
     */
    public CartViewDto getCartForUser(Long userId) {
        List<CartItem> cartItems = cartMapper.findByUserId(userId);
        List<CartItemViewDto> itemViewDtos = new ArrayList<>();

        for (CartItem item : cartItems) {
            Product product = productMapper.findById(item.getProductId());
            if (product != null) {
                itemViewDtos.add(new CartItemViewDto(
                    product.getId(),
                    product.getName(),
                    product.getImageUrl(),
                    product.getPrice(),
                    item.getQuantity()
                ));
            }
        }
        return new CartViewDto(itemViewDtos);
    }
    
    /**
     * 更新购物车中商品的数量
     */
    @Transactional
    public void updateItemQuantity(Long userId, Long productId, Integer quantity) {
        if (quantity <= 0) {
            // 如果数量小于等于0，则直接移除
            removeItemFromCart(userId, productId);
            return;
        }
        
        CartItem existingItem = cartMapper.findByUserAndProduct(userId, productId);
        if (existingItem != null) {
            existingItem.setQuantity(quantity);
            cartMapper.updateItemQuantity(existingItem);
        }
    }

    /**
     * 从购物车移除商品
     */
    @Transactional
    public void removeItemFromCart(Long userId, Long productId) {
        CartItem existingItem = cartMapper.findByUserAndProduct(userId, productId);
        if (existingItem != null) {
            cartMapper.deleteItem(existingItem.getId());
        }
    }
}