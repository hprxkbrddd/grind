package com.grind.gateway.dto.core.task;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Schema(description = "Update task payload")
@RequiredArgsConstructor
@Getter
public class ChangeTaskDTO {
    @Schema(description = "Task identifier from path", accessMode = Schema.AccessMode.READ_ONLY)
    @Setter
    private String taskId;
    @Schema(description = "Task title")
    private final String title;
    @Schema(description = "Task description")
    private final String description;
}
