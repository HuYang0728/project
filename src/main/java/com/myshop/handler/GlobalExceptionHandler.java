package com.myshop.handler;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import com.myshop.exception.ProductAlreadyExistsException; 
import com.myshop.exception.UsernameAlreadyExistsException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<Object> handleUsernameAlreadyExistsException(
            UsernameAlreadyExistsException ex, WebRequest request) {

        // 1. 创建一个有序的 Map 来存放错误详情
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.CONFLICT.value()); // 状态码 409
        body.put("error", "Conflict"); // 错误类型
        body.put("message", ex.getMessage()); // 从异常中获取我们之前设置的消息
        body.put("path", ((ServletWebRequest)request).getRequest().getRequestURI()); // 请求的URL

        // 2. 创建并返回一个 ResponseEntity 对象，状态码为 409
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }
    
    @ExceptionHandler(ProductAlreadyExistsException.class)
    public ResponseEntity<Object> handleProductAlreadyExistsException(
            ProductAlreadyExistsException ex, WebRequest request) {
        
        // 逻辑和上面的处理器完全一样，只是捕获的异常类型不同
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.CONFLICT.value()); // 状态码 409
        body.put("error", "Conflict"); // 错误类型
        body.put("message", ex.getMessage()); // 从异常中获取我们之前设置的消息
        body.put("path", ((ServletWebRequest)request).getRequest().getRequestURI());
        
        // 创建并返回一个 ResponseEntity 对象，状态码为 409
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }    
    // 你未来可以在这里添加更多 @ExceptionHandler 方法来处理其他类型的异常
    // 例如：@ExceptionHandler(ProductNotFoundException.class)
}