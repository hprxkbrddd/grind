package com.grind.gateway.dto;

import java.time.LocalDate;

public record TrackCreationDTO(
        String name,
        String description,
        Integer durationDays,
        LocalDate targetDate,
        TrackStatus status
) {
}
