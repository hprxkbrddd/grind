package com.grind.core.dto.request.task;

public record ChangeTaskRequest(
        String taskId,
        String title,
        String description) {
}
