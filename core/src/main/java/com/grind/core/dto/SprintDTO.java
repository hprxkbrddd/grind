package com.grind.core.dto;

import com.grind.core.model.Track;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record SprintDTO(
        String id,

        String name,

        LocalDate startDate,

        LocalDate endDate,

        String status,

        LocalDateTime createdAt,

        Track track) {
}
