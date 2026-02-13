package com.grind.gateway.dto;

import org.springframework.http.HttpStatus;

public record Body(
        Object payload,
        HttpStatus status
) {
    public static Body of(Object payload, HttpStatus status){
        return new Body(payload, status);
    }
}
