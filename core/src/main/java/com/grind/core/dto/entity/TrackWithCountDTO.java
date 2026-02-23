package com.grind.core.dto.entity;

import com.grind.core.enums.TrackStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TrackWithCountDTO(
        String id,
        String name,
        String description,
        String petId,
        Integer durationDays,
        Long tasks,
        LocalDate startDate,
        LocalDate targetDate,
        LocalDateTime createdAt,
        String messagePolicy,
        TrackStatus status,
        String userId
) {
}
