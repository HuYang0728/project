package com.myshop.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.myshop.model.Product;

@Mapper
public interface ProductMapper {

    Product findById(Long id);
    List<Product> findAll();
    int insertProduct(Product product);  
    int updateProduct(Product product);  
    int softDeleteById(Long id);       
    Product findByName(String name);

    /**
     * @description 根据商品ID列表，一次性查询并锁定这些商品记录
     * "FOR UPDATE" 会对查询到的行施加排他锁，防止其他事务修改这些行
     * @param productIds 商品ID列表
     * @return 商品列表
     */
    List<Product> findProductsForUpdate(List<Long> productIds);

    /**
     * @description 扣减指定商品的库存
     * @param productId 商品ID
     * @param quantity 要扣减的数量
     * @return 受影响的行数
     */
    int decreaseStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);

}