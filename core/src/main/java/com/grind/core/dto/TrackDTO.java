package com.grind.core.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TrackDTO(
        String id,

        String name,

        String description,

        String petId,

        Integer durationDays,

        LocalDate targetDate,

        LocalDateTime createdAt,

        String messagePolicy,
        
        String status
) {
}
