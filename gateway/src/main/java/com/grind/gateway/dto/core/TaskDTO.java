package com.grind.gateway.dto.core;

import com.grind.gateway.enums.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TaskDTO(
        String id,

        String title,

        String sprint_id,

        LocalDate plannedDate,

        LocalDate actualDate,

        String description,

        TaskStatus status,

        LocalDateTime createdAt
) {
}

