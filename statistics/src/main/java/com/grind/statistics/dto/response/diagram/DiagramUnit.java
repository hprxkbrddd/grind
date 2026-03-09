package com.grind.statistics.dto.response.diagram;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public record DiagramUnit(
        LocalDate day,
        @JsonProperty("completed_tasks")int completedTasks,
        @JsonProperty("planned_tasks") int plannedTasks
) {
}
