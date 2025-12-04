package com.grind.core.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.grind.core.model.Sprint;


public record TaskDTO(
        String id,

        String title,

        Sprint sprint,

        LocalDate plannedDate,

        LocalDate actualDate,

        String description,

        String status,

        LocalDateTime createdAt
) {
}
