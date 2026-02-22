package com.grind.statistics.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record OutboxRecord(
        @JsonProperty("track_id") String trackId,
        @JsonProperty("sprint_id") String sprintId,
        @JsonProperty("task_id") String taskId,
        @JsonProperty("user_id") String userId,
        Long version,
        @JsonProperty("task_status") TaskStatus taskStatus,
        @JsonProperty("changed_at") LocalDateTime changedAt
) {
}
