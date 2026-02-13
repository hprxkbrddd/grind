package com.grind.core.dto.request.task;

import java.time.LocalDate;

public record PlanTaskDateDTO(
        String taskId,
        LocalDate plannedDate
) {
}
