package com.myshop.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.myshop.exception.ProductAlreadyExistsException;
import com.myshop.mapper.ProductMapper;
import com.myshop.model.Product;

@Service
public class ProductService {

    private final ProductMapper productMapper;

    @Autowired
    public ProductService(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    public List<Product> getAllProducts() {
        return productMapper.findAll();
    }

    public Product getProductById(Long id) {
        return productMapper.findById(id);
    }

    public Product createProduct(Product product) {
        Product existingProduct = productMapper.findByName(product.getName());
        if (existingProduct != null) {
            // 如果找到了同名且未被删除的商品，就抛出异常
            throw new ProductAlreadyExistsException("商品名称 '" + product.getName() + "' 已存在!");
        }

        if (product.getStatus() == null) {
            product.setStatus("ACTIVE");
        }
        productMapper.insertProduct(product);
        return product;
    }

    public Product updateProduct(Long id, Product productDetails) {
        Product existingProduct = productMapper.findById(id);
        if (existingProduct == null) {
            return null; // 或者抛出异常
        }
        // 更新字段
        existingProduct.setName(productDetails.getName());
        existingProduct.setDescription(productDetails.getDescription());
        existingProduct.setPrice(productDetails.getPrice());
        existingProduct.setStock(productDetails.getStock());
        existingProduct.setImageUrl(productDetails.getImageUrl());
        existingProduct.setStatus(productDetails.getStatus());
        
        productMapper.updateProduct(existingProduct);
        return existingProduct;
    }

    public void deleteProduct(Long id) {
        productMapper.softDeleteById(id);
    }   
}