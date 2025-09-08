package com.myshop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping; // <-- 导入 PreAuthorize
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myshop.model.Product;
import com.myshop.service.ProductService;

@RestController
@RequestMapping("/api/admin/products")
public class ProductAdminController {

    private final ProductService productService;

    @Autowired
    public ProductAdminController(ProductService productService) {
        this.productService = productService;
    }

    // C - Create: 新增商品
    @PostMapping
    @PreAuthorize("hasAuthority('product:create')") // ★ 只有拥有 'product:create' 权限的用户才能访问
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product createdProduct = productService.createProduct(product);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    // R - Read: 根据ID查询商品
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('product:read')") // ★ 拥有 'product:read' 权限即可访问 (Admin 和 ProductManager 都有)
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        if (product != null) {
            return ResponseEntity.ok(product);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // U - Update: 更新商品
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('product:update')") // ★ 只有拥有 'product:update' 权限的用户才能访问
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
    @PreAuthorize("hasAuthority('product:delete')") // ★ 只有拥有 'product:delete' 权限的用户才能访问
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}