package com.example.wechat.exception;

public class NameNotFoundException extends RuntimeException{
    public NameNotFoundException(String message) {
        super(message);
    }
}
