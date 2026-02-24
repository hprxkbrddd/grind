package com.grind.statistics.dto.response.track;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TrackRawStatsDTO(
        @JsonProperty("track_id") String trackId,
        @JsonProperty("completed_last_30d") Long completedLastMonth,
        @JsonProperty("completed_last_7d") Long completedLastWeek
) {
}
