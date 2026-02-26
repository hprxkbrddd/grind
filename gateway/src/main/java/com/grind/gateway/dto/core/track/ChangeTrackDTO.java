package com.grind.gateway.dto.core.track;

import com.grind.gateway.enums.TrackStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Schema(description = "Update track payload")
@RequiredArgsConstructor
@Getter
public class ChangeTrackDTO {
    @Schema(description = "Track identifier from path", accessMode = Schema.AccessMode.READ_ONLY)
    @Setter
    private String id;
    @Schema(description = "Track name")
    private final String name;
    @Schema(description = "Track description")
    private final String description;
    @Schema(description = "Pet identifier")
    private final String petId;
    @Schema(description = "Start date")
    private final LocalDate startDate;
    @Schema(description = "Target date")
    private final LocalDate targetDate;
    @Schema(description = "Sprint length")
    private final Integer sprintLength;
    @Schema(description = "Message policy")
    private final String messagePolicy;
    @Schema(description = "Track status")
    private final TrackStatus status;
}
