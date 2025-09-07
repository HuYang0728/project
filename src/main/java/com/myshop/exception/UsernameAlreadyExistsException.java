package com.myshop.exception;
public class UsernameAlreadyExistsException extends RuntimeException {

    public UsernameAlreadyExistsException(String message) {
        // 调用父类（RuntimeException）的构造函数，并传入错误信息
        super(message);
    }
}
