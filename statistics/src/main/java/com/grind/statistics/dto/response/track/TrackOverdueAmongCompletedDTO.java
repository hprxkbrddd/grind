package com.grind.statistics.dto.response.track;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TrackOverdueAmongCompletedDTO(
        @JsonProperty("track_id") String trackId,
        @JsonProperty("overdue_among_active_percent") Float overdueAmongActivePercent
) {
}
