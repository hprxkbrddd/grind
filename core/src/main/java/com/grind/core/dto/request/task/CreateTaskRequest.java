package com.grind.core.dto.request.task;

public record CreateTaskRequest(
        String title,
        String trackId,
        String description
) {
}
