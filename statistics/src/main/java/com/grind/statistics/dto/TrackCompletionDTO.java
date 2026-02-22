package com.grind.statistics.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record TrackCompletionDTO(
        @JsonProperty("track_id") Long trackId,
        @JsonProperty("completion_percent") BigDecimal completionPercent,
        @JsonProperty("total_tasks") Long totalTasks
) {
}
