package com.grind.core.request.Track;

import com.grind.core.dto.TrackStatus;

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
