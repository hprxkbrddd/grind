package com.grind.gateway.dto;

public record CoreMessageDTO(
        CoreMessageType type,
        String payload
) {
}
