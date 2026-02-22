package com.grind.statistics.dto.wrap;

import com.grind.statistics.dto.StatisticsMessageType;
import org.springframework.http.HttpStatus;

public record Reply<T>(
        StatisticsMessageType type,
        Body<T> body
) {
    public static <T> Reply<T> ok(StatisticsMessageType type, T payload) {
        return new Reply<>(
                type,
                Body.ok(payload)
        );
    }

    public static <T> Reply<T> error(Throwable ex, HttpStatus status) {
        return new Reply<>(
                StatisticsMessageType.ERROR,
                Body.err(ex.getMessage(), status)
        );
    }
}
