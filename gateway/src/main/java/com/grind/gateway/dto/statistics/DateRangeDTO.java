package com.grind.gateway.dto.statistics;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Exclusive date range for filtering diagram statistics")
public record DateRangeDTO(
        @Schema(description = "Exclusive range start date", example = "2026-03-01")
        LocalDate startDate,
        @Schema(description = "Exclusive range end date", example = "2026-03-31")
        LocalDate endDate
) {
}
