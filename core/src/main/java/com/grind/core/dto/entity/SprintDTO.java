package com.grind.core.dto.entity;

import java.time.LocalDate;

public record SprintDTO(
        String id,

        LocalDate startDate,

        LocalDate endDate,

        String track_id) {
}
