package com.grind.core.dto;

import java.time.LocalDate;

public record PlanTaskDateDTO(
        String taskId,
        LocalDate plannedDate
) {
}
