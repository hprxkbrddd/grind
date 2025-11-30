package com.grind.gateway.dto.core;

import com.grind.gateway.enums.TrackStatus;

import java.time.LocalDate;

public record TrackCreationDTO(
        String name,
        String description,
        Integer durationDays,
        LocalDate targetDate,
        TrackStatus status
) {
}
