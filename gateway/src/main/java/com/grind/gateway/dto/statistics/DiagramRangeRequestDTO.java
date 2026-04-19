package com.grind.gateway.dto.statistics;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public record DiagramRangeRequestDTO(
        @JsonProperty("track_id") String trackId,
        @JsonProperty("start_date") LocalDate startDate,
        @JsonProperty("end_date") LocalDate endDate
) {
    public static DiagramRangeRequestDTO of(String trackId, DateRangeDTO range){
        return new DiagramRangeRequestDTO(
                trackId,
                range == null ? null : range.startDate(),
                range == null ? null : range.endDate()
        );
    }
}
