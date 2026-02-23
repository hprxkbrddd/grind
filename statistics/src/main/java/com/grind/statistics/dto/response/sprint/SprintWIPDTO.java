package com.grind.statistics.dto.response.sprint;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SprintWIPDTO(
        @JsonProperty("sprint_id") String sprintId,
        @JsonProperty("active_wip") Integer activeWip
) {
}
