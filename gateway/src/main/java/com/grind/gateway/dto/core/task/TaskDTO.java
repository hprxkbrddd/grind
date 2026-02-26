package com.grind.gateway.dto.core.task;

import com.grind.gateway.enums.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "Task data")
public record TaskDTO(
        @Schema(description = "Task identifier")
        String id,
        @Schema(description = "Task title")
        String title,
        @Schema(description = "Sprint identifier")
        String sprint_id,
        @Schema(description = "Track identifier")
        String track_id,
        @Schema(description = "Planned date")
        LocalDate plannedDate,
        @Schema(description = "Actual completion date")
        LocalDate actualDate,
        @Schema(description = "Task description")
        String description,
        @Schema(description = "Task status")
        TaskStatus status,
        @Schema(description = "Creation timestamp")
        LocalDateTime createdAt,
        @Schema(description = "Entity version")
        Long version
) {
}
