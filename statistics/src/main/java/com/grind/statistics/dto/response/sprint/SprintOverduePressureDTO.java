package com.grind.statistics.dto.response.sprint;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SprintOverduePressureDTO(
        @JsonProperty("sprint_id") String sprintId,
        @JsonProperty("overdue_percent") Float overduePercent
) {
}
