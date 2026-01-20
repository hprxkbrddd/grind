package com.grind.core.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;


public record TaskDTO(
        String id,

        String title,

        String sprint_id,

        LocalDate plannedDate,

        LocalDate actualDate,

        String description,

        String status,

        LocalDateTime createdAt
) {
}
