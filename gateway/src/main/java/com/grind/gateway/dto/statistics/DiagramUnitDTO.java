package com.grind.gateway.dto.statistics;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Single point on a statistics diagram")
public record DiagramUnitDTO(
        @Schema(description = "Aggregation date", example = "2026-03-03")
        LocalDate day,

        @JsonProperty("completed_tasks")
        @Schema(description = "Completed tasks for the day or week", example = "3")
        int completedTasks,

        @JsonProperty("planned_tasks")
        @Schema(description = "Planned tasks for the day or week", example = "5")
        int plannedTasks
) {
}
