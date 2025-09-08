package com.myshop.controller;

import java.security.Principal; // ★ 需要创建一个新的DTO来接收请求

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myshop.dto.AddToCartRequestDto;
import com.myshop.dto.CartViewDto;
import com.myshop.dto.UpdateQuantityRequestDto;
import com.myshop.model.User;
import com.myshop.service.CartService;
import com.myshop.service.UserService;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final UserService userService; // 需要用它来根据用户名获取完整的User对象

    @Autowired
    public CartController(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

    @PostMapping("/items")
    public ResponseEntity<?> addItemToCart(@RequestBody AddToCartRequestDto request, Principal principal) {
        // Principal 对象由 Spring Security 自动注入，代表当前登录的用户
        if (principal == null) {
            // 一般来说，JwtAuthenticationFilter 会拦截，这里是双重保险
            return ResponseEntity.status(401).body("请先登录");
        }
        
        // 从 Principal 获取用户名，然后从数据库获取完整的用户信息（主要是userId）
        String username = principal.getName();
        User user = userService.getUserByUsername(username);

        // 调用 Service 层的核心逻辑
        cartService.addItemToCart(user.getId(), request.getProductId(), request.getQuantity());

        return ResponseEntity.ok("商品已成功添加到购物车");
    }

    @GetMapping("") // GET /api/cart
    public ResponseEntity<?> getMyCart(Principal principal) {
        String username = principal.getName();
        User user = userService.getUserByUsername(username);
        
        CartViewDto cartView = cartService.getCartForUser(user.getId());
        return ResponseEntity.ok(cartView);
    }

    @PutMapping("/items/{productId}") // PUT /api/cart/items/{productId}
    public ResponseEntity<?> updateItemQuantity(@PathVariable Long productId,
                                                @RequestBody UpdateQuantityRequestDto request,
                                                Principal principal) {
        String username = principal.getName();
        User user = userService.getUserByUsername(username);

        cartService.updateItemQuantity(user.getId(), productId, request.getQuantity());
        return ResponseEntity.ok("购物车商品数量已更新");
    }

    @DeleteMapping("/items/{productId}") // DELETE /api/cart/items/{productId}
    public ResponseEntity<?> removeItemFromCart(@PathVariable Long productId, Principal principal) {
        String username = principal.getName();
        User user = userService.getUserByUsername(username);

        cartService.removeItemFromCart(user.getId(), productId);
        return ResponseEntity.ok("商品已从购物车移除");
    }
}