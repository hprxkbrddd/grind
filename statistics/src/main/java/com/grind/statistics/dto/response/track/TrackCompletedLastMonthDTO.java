package com.grind.statistics.dto.response.track;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TrackCompletedLastMonthDTO(
        @JsonProperty("track_id") String trackId,
        @JsonProperty("completed_last_30d") Integer completedLast30d
) {
}
