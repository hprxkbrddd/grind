package com.grind.statistics.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TrackWIPDTO(
        @JsonProperty("track_id") String trackId,
        @JsonProperty("active_wip") Integer activeWip
) {
}
