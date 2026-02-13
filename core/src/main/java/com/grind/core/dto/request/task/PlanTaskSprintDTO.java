package com.grind.core.dto.request.task;

public record PlanTaskSprintDTO(
        String taskId,
        String sprintId,
        Integer dayOfSprint
) {
}
