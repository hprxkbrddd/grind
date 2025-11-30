package com.grind.gateway.dto.core;

import com.grind.gateway.enums.CoreMessageType;

public record CoreMessageDTO(
        CoreMessageType type,
        String payload
) {
}
