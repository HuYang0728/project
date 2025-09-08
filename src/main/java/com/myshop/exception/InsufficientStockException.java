package com.myshop.exception;

/**
 * @description 当商品库存不足时抛出此异常
 */
public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String message) {
        super(message);
    }
}
