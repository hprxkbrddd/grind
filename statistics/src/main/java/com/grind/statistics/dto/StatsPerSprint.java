package com.grind.statistics.dto;

public record StatsPerSprint(
        String sprintId,
        Integer tasksTotal,
        Integer tasksCompleted,
        Integer tasksOverdue,
        Integer tasksPlanned
) {
}
