package com.grind.statistics.dto.response.sprint;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SprintActiveTasksAgingDTO(
        @JsonProperty("sprint_id") String sprintId,
        @JsonProperty("avg_active_age_days") Float avgActiveAgeDays
) {
}
