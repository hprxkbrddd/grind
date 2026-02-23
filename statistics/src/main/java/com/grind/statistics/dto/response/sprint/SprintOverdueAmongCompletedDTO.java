package com.grind.statistics.dto.response.sprint;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SprintOverdueAmongCompletedDTO(
        @JsonProperty("sprint_id") String sprintId,
        @JsonProperty("overdue_among_active_percent") Float overdueAmongActivePercent
) {
}
