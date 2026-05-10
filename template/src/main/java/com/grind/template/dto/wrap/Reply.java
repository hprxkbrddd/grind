package com.grind.template.dto.wrap;

import com.grind.template.enums.TemplateMessageType;
import org.springframework.http.HttpStatus;

public record Reply<T>(
        TemplateMessageType type,
        Body<T> body
) {
    public static <T> Reply<T> ok(TemplateMessageType type, T payload) {
        return new Reply<>(
                type,
                Body.ok(payload)
        );
    }

    public static <T> Reply<T> error(Throwable ex, HttpStatus status) {
        return new Reply<>(
                TemplateMessageType.ERROR,
                Body.err(ex.getMessage(), status)
        );
    }
}
