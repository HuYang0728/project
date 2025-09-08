package com.myshop.handler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import com.myshop.exception.EmptyCartException;
import com.myshop.exception.InsufficientStockException;
import com.myshop.exception.ProductAlreadyExistsException; 
import com.myshop.exception.UsernameAlreadyExistsException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<Object> handleUsernameAlreadyExistsException(
            UsernameAlreadyExistsException ex, WebRequest request) {

        // 3.1 创建一个 Map (我们未来的 JSON 对象)
        Map<String, Object> body = new LinkedHashMap<>();
        // 3.2 使用 .put() 方法，一步步填充错误信息
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.CONFLICT.value()); // 状态码 409
        body.put("error", "Conflict"); // 错误类型
        body.put("message", ex.getMessage()); // 从异常中获取我们之前设置的消息
        body.put("path", ((ServletWebRequest)request).getRequest().getRequestURI()); // 请求的URL


        // 4. 返回 ResponseEntity
        // ResponseEntity 是 Spring 用来封装整个 HTTP 响应的对象。
        // 我们告诉它：
        //   - 响应体(body)是 我们构建好的 Map (它会被转成 JSON)
        //   - HTTP 状态码(status)是 409 Conflict
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
        /**
     * @description 专门处理“空购物车”异常
     * @param ex 捕获到的 EmptyCartException 异常对象
     * @return 返回一个 HTTP 400 Bad Request 响应
     */
    @ExceptionHandler(EmptyCartException.class)
    public ResponseEntity<Map<String, String>> handleEmptyCartException(EmptyCartException ex) {
        // 创建一个简单的Map来构造JSON响应体
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Bad Request");
        errorResponse.put("message", ex.getMessage()); // 获取在Service中设置的异常信息
        
        // 返回 400 状态码和错误信息
        // 400 Bad Request: 表示客户端发送了一个服务器无法处理的请求（比如对空购物车下单）
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * @description 专门处理“库存不足”异常
     * @param ex 捕获到的 InsufficientStockException 异常对象
     * @return 返回一个 HTTP 409 Conflict 响应
     */
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<Map<String, String>> handleInsufficientStockException(InsufficientStockException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Conflict");
        errorResponse.put("message", ex.getMessage());
        
        // 返回 409 状态码和错误信息
        // 409 Conflict: 表示请求与服务器当前状态冲突（比如库存已经没了）
        // 这是一个比 400 更精确、更专业的的状态码
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }
    // 你未来可以在这里添加更多 @ExceptionHandler 方法来处理其他类型的异常
    // 例如：@ExceptionHandler(ProductNotFoundException.class)
}