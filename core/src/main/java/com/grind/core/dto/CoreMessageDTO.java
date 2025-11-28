package com.grind.core.dto;

public record CoreMessageDTO(
        CoreMessageType type,
        String payload
) {
}
