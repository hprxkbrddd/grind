package com.grind.core.dto.wrap;

import com.grind.core.enums.CoreMessageType;
import org.springframework.http.HttpStatus;

public record Reply<T>(
        CoreMessageType type,
        Body<T> body
) {
    public static <T> Reply<T> ok(CoreMessageType type, T payload) {
        return new Reply<>(
                type,
                Body.ok(payload)
        );
    }

    public static <T> Reply<T> error(Throwable ex, HttpStatus status) {
        return new Reply<>(
                CoreMessageType.ERROR,
                Body.err(ex.getMessage(), status)
        );
    }
}
