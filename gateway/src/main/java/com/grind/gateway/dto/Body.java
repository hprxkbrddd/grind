package com.grind.gateway.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;

@Schema(description = "Standard response wrapper")
public record Body<T>(
        @Schema(description = "Response payload")
        T payload,
        @Schema(description = "HTTP status")
        HttpStatus status,
        @Schema(description = "Error message")
        String error
) {
    public static <T> Body<T> ok(T payload) {
        return new Body<>(payload, HttpStatus.OK, null);
    }

    public static <T> Body<T> err(String error, HttpStatus status) {
        return new Body<>(null, status, error);
    }
}
