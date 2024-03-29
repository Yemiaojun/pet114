package com.example.wechat;

import com.example.wechat.exception.DefaultException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import utils.Result;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DefaultException.class)
    public ResponseEntity<String> handleDefaultException(DefaultException ex) {
        return ResponseEntity.status(ex.getStatus()).body(Result.errorGetString(ex.getMessage()));
    }

}
