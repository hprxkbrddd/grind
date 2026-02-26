package com.grind.gateway.dto.core.task;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Create task request")
public record CreateTaskRequest(
        @Schema(description = "Task title")
        String title,
        @Schema(description = "Task description")
        String description,
        @Schema(description = "Track identifier")
        String trackId
) {
}
