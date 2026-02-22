package com.grind.gateway.dto;

import org.springframework.http.HttpStatus;

public record Body<T>(
        T payload,
        HttpStatus status,
        String error
) {
    public static <T> Body<T> ok(T payload) {
        return new Body<>(payload, HttpStatus.OK, null);
    }

    public static <T> Body<T> err(String error, HttpStatus status) {
        return new Body<>(null, status, error);
    }
}

