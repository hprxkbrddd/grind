package com.grind.core.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record SprintDTO(
        String id,

        String name,

        LocalDate startDate,

        LocalDate endDate,

        String status,

        LocalDateTime createdAt,

        String track_id) {
}
