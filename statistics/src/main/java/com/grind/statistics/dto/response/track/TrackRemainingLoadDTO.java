package com.grind.statistics.dto.response.track;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TrackRemainingLoadDTO(
        @JsonProperty("track_id") String trackId,
        @JsonProperty("remaining_tasks") Integer remainingTasks
) {
}
