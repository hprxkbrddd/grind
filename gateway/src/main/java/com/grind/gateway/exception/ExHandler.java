package com.grind.gateway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.concurrent.TimeoutException;

@RestControllerAdvice
public class ExHandler {
    @ExceptionHandler(TimeoutException.class)
    public ResponseEntity<Object> handleTimeoutException(
            TimeoutException ex
    ){
        return ResponseEntity
                .status(HttpStatus.GATEWAY_TIMEOUT)
                .body(ex);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Object> handleIllegalStateException(
            IllegalStateException ex
    ){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex);
    }
}
