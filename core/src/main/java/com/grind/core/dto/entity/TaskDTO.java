package com.grind.core.dto.entity;

import com.grind.core.enums.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;


public record TaskDTO(
        String id,
        String title,
        String sprint_id,
        String track_id,
        LocalDate plannedDate,
        LocalDate actualDate,
        String description,
        TaskStatus status,
        LocalDateTime createdAt,
        Long version
) {
}
