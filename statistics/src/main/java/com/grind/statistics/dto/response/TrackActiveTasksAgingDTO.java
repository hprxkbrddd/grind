package com.grind.statistics.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TrackActiveTasksAgingDTO(
        @JsonProperty("track_id") String trackId,
        @JsonProperty("avg_active_age_days") Float avgActiveAgeDays
) {
}
