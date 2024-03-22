package com.example.wechat.exception;

public class NameAlreadyExistedException extends RuntimeException{
    public NameAlreadyExistedException(String message) {
        super(message);
    }
}
