package com.grind.statistics.dto.response.track;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TrackOverduePressureDTO(
        @JsonProperty("track_id") String trackId,
        @JsonProperty("overdue_percent") Float overduePercent
) {
}
