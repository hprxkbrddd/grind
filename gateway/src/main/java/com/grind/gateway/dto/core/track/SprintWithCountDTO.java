package com.grind.gateway.dto.core.track;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Sprint with task count data")
public record SprintWithCountDTO(
        @Schema(description = "Sprint identifier")
        String id,
        @Schema(description = "Sprint start date")
        LocalDate startDate,
        @Schema(description = "Sprint end date")
        LocalDate endDate,
        @Schema(description = "Track identifier")
        String track_id,
        @Schema(description = "Task count")
        Long tasks
) {
}
