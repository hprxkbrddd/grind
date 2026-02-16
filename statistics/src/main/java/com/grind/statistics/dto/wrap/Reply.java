package com.grind.statistics.dto.wrap;

import com.grind.statistics.enums.SystemMsgType;
import org.springframework.http.HttpStatus;

public record Reply<T>(
        MessageType type,
        Body<T> body
) {
    public static <T> Reply<T> ok(MessageType type, T payload) {
        return new Reply<>(
                type,
                Body.ok(payload)
        );
    }

    public static <T> Reply<T> error(Throwable ex, HttpStatus status) {
        return new Reply<>(
                SystemMsgType.ERROR,
                Body.err(ex.getMessage(), status)
        );
    }
}
