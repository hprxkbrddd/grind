package com.grind.core.request.Track;

import com.grind.core.dto.TrackStatus;

import java.time.LocalDate;

public record ChangeTrackRequest(
        String id,
        String name,
        String description,
        String petId,
        LocalDate startDate,
        LocalDate targetDate,
        Integer sprintLength,
        String messagePolicy,
        TrackStatus status) {

}
