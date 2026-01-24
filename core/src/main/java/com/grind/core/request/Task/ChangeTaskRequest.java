package com.grind.core.request.Task;

public record ChangeTaskRequest(
        String taskId,
        String title,
        String description) {
}
