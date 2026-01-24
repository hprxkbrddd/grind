package com.grind.core.dto;

public record PlanTaskSprintDTO(
        String taskId,
        String sprintId,
        Integer dayOfSprint
) {
}
