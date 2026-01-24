package com.grind.core.dto;

import org.springframework.http.HttpStatus;

public record Reply(
        CoreMessageType type,
        Object payload,
        HttpStatus status
) {
    public static Reply ok(CoreMessageType type, Object payload) {
        return new Reply(type, payload, HttpStatus.OK);
    }

    public static Reply error(
            Throwable ex,
            HttpStatus status
    ) {
        return new Reply(
                CoreMessageType.ERROR,
                ex.getMessage(),
                status
        );
    }
}
