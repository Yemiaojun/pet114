package com.example.wechat.exception;

import org.springframework.http.HttpStatus;

public class DefaultException extends RuntimeException {
    private final   HttpStatus status; // 可以为异常指定一个HTTP状态码

    public DefaultException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST; // 默认状态码，可以根据需要调整
    }

    public DefaultException(String message, HttpStatus status) {
        super(message);
        this.status = status; // 允许指定不同的HTTP状态码
    }

    public HttpStatus getStatus() {
        return status;
    }
}
