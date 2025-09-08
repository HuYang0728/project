package com.myshop.exception;

/**
 * @description 当用户尝试从空购物车创建订单时抛出此异常
 */
public class EmptyCartException extends RuntimeException {
    public EmptyCartException(String message) {
        super(message);
    }
}