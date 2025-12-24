package com.grind.statistics.dto;

import java.sql.Timestamp;

public record CoreRecord(
        String eventId,
        String trackId,
        String sprintId,
        String taskId,
        Integer version,
        TaskStatus taskStatus,
        Timestamp changedAt
) {
}
