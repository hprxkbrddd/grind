package com.grind.core.dto.wrap;

import com.grind.core.enums.CoreMessageType;
import org.springframework.http.HttpStatus;

public record Reply(
        CoreMessageType type,
        Body body
) {
    public static Reply ok(CoreMessageType type, Object payload) {
        return new Reply(
                type,
                Body.of(payload, HttpStatus.OK)
        );
    }

    public static Reply error(Throwable ex, HttpStatus status) {
        return new Reply(
                CoreMessageType.ERROR,
                Body.of(ex.getMessage(), status)
        );
    }
}
