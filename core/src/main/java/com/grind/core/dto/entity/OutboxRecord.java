package com.grind.core.dto.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.grind.core.enums.TaskStatus;

import java.time.Instant;

public record OutboxRecord(
        @JsonProperty("track_id") String trackId,
        @JsonProperty("sprint_id") String sprintId,
        @JsonProperty("task_id") String taskId,
        Long version,
        @JsonProperty("task_status") TaskStatus taskStatus,
        @JsonProperty("changed_at") Instant changedAt
) {
}
