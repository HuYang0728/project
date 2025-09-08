// 文件路径: com/myshop/dto/CreateOrderRequestDto.java

package com.myshop.dto;

import lombok.Data;

/**
 * @description 用于接收前端创建订单请求的数据传输对象
 */
@Data
public class CreateOrderRequestDto {
    // 目前我们只要求前端提供一个收货地址
    // 在实际项目中，可能还会有优惠券ID、备注等信息
    private String shippingAddress;
}