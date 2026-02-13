package com.grind.gateway.dto.core.task;

public record CreateTaskRequest(
        String title,
        String description,
        String trackId
) {
}
