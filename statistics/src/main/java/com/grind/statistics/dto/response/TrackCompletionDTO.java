package com.grind.statistics.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record TrackCompletionDTO(
        @JsonProperty("track_id") String trackId,
        @JsonProperty("completion_percent") BigDecimal completionPercent,
        @JsonProperty("total_tasks") Integer totalTasks
) {
}
