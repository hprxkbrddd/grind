package com.grind.gateway.dto.core;

import com.grind.gateway.enums.TrackStatus;

import java.time.LocalDate;

public record CreateTrackRequest(
        String name,
        String description,
        String petId,
        Integer sprintLength,
        LocalDate startDate,
        LocalDate targetDate,
        String messagePolicy,
        TrackStatus status
) {
}
