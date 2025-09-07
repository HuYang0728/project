package com.myshop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myshop.model.Product;
import com.myshop.service.ProductService;

@RestController
@RequestMapping("/api/admin/products") // 注意路径，区分于普通用户接口
public class ProductAdminController {

    private final ProductService productService;

    @Autowired
    public ProductAdminController(ProductService productService) {
        this.productService = productService;
    }

    // C - Create: 新增商品
    @PostMapping
    // TODO: 记得加上管理员权限验证！
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product createdProduct = productService.createProduct(product);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    // U - Update: 更新商品
    @PutMapping("/{id}")
    // TODO: 记得加上管理员权限验证！
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product productDetails) {
        Product updatedProduct = productService.updateProduct(id, productDetails);
        if (updatedProduct != null) {
            return ResponseEntity.ok(updatedProduct);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // D - Delete: 删除商品 (软删除)
    @DeleteMapping("/{id}")
    // TODO: 记得加上管理员权限验证！
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{id}")
    // TODO: 将来在这里添加 Spring Security 权限控制
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        if (product != null) {
            return ResponseEntity.ok(product);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}