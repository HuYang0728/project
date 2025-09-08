package com.myshop.mapper;

import com.myshop.model.CartItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CartMapper {

    /**
     * 根据用户ID查询其所有的购物车项目
     */
    List<CartItem> findByUserId(Long userId);

    /**
     * 根据用户ID和商品ID查询单个购物车项目
     * (用于判断商品是否已在购物车中)
     */
    CartItem findByUserAndProduct(@Param("userId") Long userId, @Param("productId") Long productId);

    /**
     * 插入一个新的购物车项目
     */
    int insertItem(CartItem cartItem);

    /**
     * 更新一个已存在的购物车项目的数量
     */
    int updateItemQuantity(CartItem cartItem);

    /**
     * 根据ID删除一个购物车项目
     */
    int deleteItem(Long cartItemId);
    
    /**
     * 根据用户ID清空其所有购物车项目
     * (下单成功后调用)
     */
    int deleteByUserId(Long userId);
}