package com.grind.gateway.dto.core.track;

import com.grind.gateway.enums.TrackStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "Track data")
public record TrackDTO(
        @Schema(description = "Track identifier")
        String id,
        @Schema(description = "Track name")
        String name,
        @Schema(description = "Track description")
        String description,
        @Schema(description = "Pet identifier")
        String petId,
        @Schema(description = "Track duration in days")
        Integer durationDays,
        @Schema(description = "Start date")
        LocalDate startDate,
        @Schema(description = "Target date")
        LocalDate targetDate,
        @Schema(description = "Created timestamp")
        LocalDateTime createdAt,
        @Schema(description = "Message policy")
        String messagePolicy,
        @Schema(description = "Track status")
        TrackStatus status,
        @Schema(description = "User identifier")
        String userId
) {
}
