package com.grind.core.dto.request.track;

import com.grind.core.enums.TrackStatus;

import java.time.LocalDate;

public record CreateTrackRequest(
        String name,
        String description,
        String petId,
        Integer sprintLength,
        LocalDate startDate,
        LocalDate targetDate,
        String messagePolicy,
        TrackStatus status) {
}
