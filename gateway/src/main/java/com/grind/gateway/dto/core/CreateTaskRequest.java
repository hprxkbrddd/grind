package com.grind.gateway.dto.core;

public record CreateTaskRequest(
        String title,
        String description,
        String trackId
) {
}
