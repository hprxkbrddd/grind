package com.grind.statistics.dto;

public record CoreMessageDTO(
        CoreMessageType type,
        CoreRecord payload
) {
}
