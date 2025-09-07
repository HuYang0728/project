package com.myshop.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.myshop.model.Product;

@Mapper
public interface ProductMapper {

    Product findById(Long id);
    List<Product> findAll();
    int insertProduct(Product product);  
    int updateProduct(Product product);  
    int softDeleteById(Long id);       
    Product findByName(String name);
}