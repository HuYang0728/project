package com.myshop.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.myshop.model.Order;
import com.myshop.model.OrderItem;

@Mapper
public interface OrderMapper {

    /**
     * 插入订单主表记录
     * (MyBatis会通过 useGeneratedKeys 将生成的主键ID回填到 order 对象中)
     */
    int insertOrder(Order order);

    /**
     * 批量插入订单项目记录
     */
    int insertOrderItems(@Param("items") List<OrderItem> items);

    /**
     * 根据用户ID查询其所有订单 (通常用于订单历史列表)
     * (注意：这个查询可以先不加载订单项，以提高列表页性能)
     */
    List<Order> findOrdersByUserId(Long userId);

    /**
     * 根据订单ID查询单个订单的完整信息，包括其所有的订单项
     */
    Order findOrderById(Long orderId);
    
    /**
     * 根据业务订单号查询订单
     */
    Order findByOrderNo(String orderNo);

}