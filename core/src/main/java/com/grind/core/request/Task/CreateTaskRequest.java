package com.grind.core.request.Task;

public record CreateTaskRequest(
        String title,
        String trackId,
        String description
) {
}
