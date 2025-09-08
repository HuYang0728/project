package com.myshop.controller;

import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myshop.dto.CreateOrderRequestDto;
import com.myshop.dto.OrderViewDto;
import com.myshop.model.Order;
import com.myshop.model.User;
import com.myshop.service.OrderService;
import com.myshop.service.UserService;

/**
 * @description 订单相关的API接口
 */
@RestController
@RequestMapping("/api/orders") // 所有与订单相关的接口，都以 /api/orders 开头
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    @Autowired
    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    /**
     * @description 从购物车创建新订单
     * @param request 包含收货地址等信息的请求体
     * @param principal Spring Security 注入的当前登录用户信息
     * @return 创建成功后的订单详情
     */
    @PostMapping // 对应请求 POST /api/orders
    public ResponseEntity<OrderViewDto> createOrder(@RequestBody CreateOrderRequestDto request, Principal principal) {
        // 1. 获取当前登录用户的ID
        String username = principal.getName();
        User user = userService.getUserByUsername(username);

        // 2. 调用核心业务逻辑
        Order createdOrder = orderService.createOrderFromCart(user.getId(), request.getShippingAddress());

        // 3. 将业务层返回的 Order 实体对象，转换为对前端友好的 DTO 对象
        OrderViewDto orderDto = OrderViewDto.from(createdOrder);
        
        // 4. 返回 HTTP 201 Created 状态码，表示资源创建成功，并在响应体中包含新创建的订单信息
        return ResponseEntity.status(HttpStatus.CREATED).body(orderDto);
    }

    /**
     * @description 获取当前登录用户的所有订单历史
     * @param principal 当前登录用户信息
     * @return 订单列表
     */
    @GetMapping // 对应请求 GET /api/orders
    public ResponseEntity<List<OrderViewDto>> getMyOrders(Principal principal) {
        // 1. 获取用户ID
        User user = userService.getUserByUsername(principal.getName());
        
        // 2. 查询该用户的所有订单
        List<Order> orders = orderService.getOrdersForUser(user.getId());
        
        // 3. 将 Order 实体列表转换为 DTO 列表
        List<OrderViewDto> orderDtos = orders.stream()
                                             .map(OrderViewDto::from)
                                             .collect(Collectors.toList());
        
        return ResponseEntity.ok(orderDtos);
    }
    
    /**
     * @description 根据订单号查询单个订单的详情
     * @param orderNo URL路径中的订单号
     * @param principal 当前登录用户信息
     * @return 订单详情
     */
    @GetMapping("/{orderNo}") // 对应请求 GET /api/orders/{orderNo}
    public ResponseEntity<OrderViewDto> getOrderByOrderNo(@PathVariable String orderNo, Principal principal) {
        // 1. 获取用户信息
        User user = userService.getUserByUsername(principal.getName());

        // 2. 查询订单
        Order order = orderService.getOrderByOrderNo(orderNo);
        
        // 3. ★★★ 安全校验 ★★★
        // 检查订单是否存在，以及该订单是否属于当前登录的用户
        if (order == null || !Objects.equals(order.getUserId(), user.getId())) {
            // 如果订单不存在或不属于该用户，返回 404 Not Found
            // 不返回 403 Forbidden 是为了避免泄露“该订单号确实存在”这个信息
            return ResponseEntity.notFound().build();
        }
        
        // 4. 转换为 DTO 并返回
        return ResponseEntity.ok(OrderViewDto.from(order));
    }
}