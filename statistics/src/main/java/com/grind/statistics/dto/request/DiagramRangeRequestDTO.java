package com.grind.statistics.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public record DiagramRangeRequestDTO(
        @JsonProperty("track_id") String trackId,
        @JsonProperty("start_date") LocalDate startDate,
        @JsonProperty("end_date") LocalDate endDate
) {
}
