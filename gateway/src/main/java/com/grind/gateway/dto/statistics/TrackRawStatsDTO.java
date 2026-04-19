package com.grind.gateway.dto.statistics;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Track completion statistics for recent periods")
public record TrackRawStatsDTO(
        @JsonProperty("track_id")
        @Schema(description = "Track identifier", example = "track-1")
        String trackId,

        @JsonProperty("completed_last_30d")
        @Schema(description = "Completed tasks during the last 30 days", example = "12")
        Long completedLastMonth,

        @JsonProperty("completed_last_7d")
        @Schema(description = "Completed tasks during the last 7 days", example = "4")
        Long completedLastWeek
) {
}
