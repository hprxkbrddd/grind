package com.grind.statistics.dto.response.sprint;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SprintRemainingLoadDTO(
        @JsonProperty("sprint_id") String sprintId,
        @JsonProperty("remaining_tasks") Integer remainingTasks
) {
}
