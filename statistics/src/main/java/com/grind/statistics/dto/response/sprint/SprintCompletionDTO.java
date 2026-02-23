package com.grind.statistics.dto.response.sprint;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record SprintCompletionDTO(
        @JsonProperty("sprint_id") String sprintId,
        @JsonProperty("completion_percent") BigDecimal completionPercent,
        @JsonProperty("total_tasks") Integer totalTasks
) {
}
