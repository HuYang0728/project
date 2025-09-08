// 文件路径: com/myshop/service/OrderService.java

package com.myshop.service;

import com.myshop.exception.EmptyCartException;
import com.myshop.exception.InsufficientStockException;
import com.myshop.mapper.CartMapper;
import com.myshop.mapper.OrderMapper;
import com.myshop.mapper.ProductMapper;
import com.myshop.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderMapper orderMapper;
    private final CartMapper cartMapper;
    private final ProductMapper productMapper;

    @Autowired
    public OrderService(OrderMapper orderMapper, CartMapper cartMapper, ProductMapper productMapper) {
        this.orderMapper = orderMapper;
        this.cartMapper = cartMapper;
        this.productMapper = productMapper;
    }

    /**
     * @description 从购物车创建订单的完整业务逻辑
     * @param userId 用户ID
     * @param shippingAddress 收货地址
     * @return 创建好的订单对象
     */
    @Transactional // ★★★ 整个方法作为一个数据库事务来执行 ★★★
    public Order createOrderFromCart(Long userId, String shippingAddress) {
        // 1. 获取用户的购物车商品
        List<CartItem> cartItems = cartMapper.findByUserId(userId);
        if (cartItems.isEmpty()) {
            throw new EmptyCartException("购物车是空的，无法创建订单");
        }

        // 2. 锁定库存：一次性查询并锁定所有相关商品，防止并发问题
        List<Long> productIds = cartItems.stream().map(CartItem::getProductId).collect(Collectors.toList());
        List<Product> products = productMapper.findProductsForUpdate(productIds);
        
        // 为了方便后续查找，将 List<Product> 转换为 Map<ProductId, Product>
        Map<Long, Product> productMap = products.stream()
            .collect(Collectors.toMap(Product::getId, Function.identity()));

        // 3. 检查库存是否充足
        for (CartItem item : cartItems) {
            Product product = productMap.get(item.getProductId());
            if (product == null || product.getStock() < item.getQuantity()) {
                throw new InsufficientStockException("商品 '" + (product != null ? product.getName() : "ID:"+item.getProductId()) + "' 库存不足");
            }
        }

        // 4. 计算订单总价
        BigDecimal totalAmount = cartItems.stream()
            .map(item -> productMap.get(item.getProductId()).getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 5. 创建订单主记录 (Order)
        Order order = new Order();
        order.setOrderNo(generateOrderNo()); // 生成业务订单号
        order.setUserId(userId);

        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.PENDING_PAYMENT); // 初始状态为待付款
        order.setShippingAddress(shippingAddress);
        
        // 插入 Order 记录到数据库，MyBatis 会将生成的主键ID回填到 order 对象
        orderMapper.insertOrder(order);
        Long orderId = order.getId();

        // 6. 创建订单项列表 (OrderItem List)，即“交易快照”
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            Product product = productMap.get(cartItem.getProductId());
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(orderId);

            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName()); // 快照
            orderItem.setProductImageUrl(product.getImageUrl()); // 快照
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtPurchase(product.getPrice()); // 快照
            
            orderItems.add(orderItem);
        }

        // 7. 批量插入订单项
        orderMapper.insertOrderItems(orderItems);

        // 8. 扣减所有商品库存
        for (CartItem cartItem : cartItems) {
            int rowsAffected = productMapper.decreaseStock(cartItem.getProductId(), cartItem.getQuantity());
            if (rowsAffected == 0) {
                 // 如果扣减库存失败（比如并发情况下库存刚好被买完），则抛出异常，整个事务会回滚
                 throw new InsufficientStockException("扣减商品ID: " + cartItem.getProductId() + " 库存失败");
            }
        }

        // 9. 清空用户的购物车
        cartMapper.deleteByUserId(userId);
        
        // 10. 返回创建好的完整订单信息
        order.setOrderItems(orderItems); // 填充订单项信息
        return order;
    }
    
    // 其他查询方法
    public List<Order> getOrdersForUser(Long userId) {
        return orderMapper.findOrdersByUserId(userId);
    }

    public Order getOrderByOrderNo(String orderNo) {
        return orderMapper.findByOrderNo(orderNo);
    }
    
    /**
     * @description 生成一个唯一的业务订单号
     * @return 订单号字符串
     */
    private String generateOrderNo() {
        // 一个简单的订单号生成策略：当前时间戳 + UUID的前8位
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return timestamp + uuid;
    }
}