package com.grind.statistics.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.grind.statistics.enums.TaskStatus;

import java.time.LocalDateTime;

public record StatisticsEventDTO(
        @JsonProperty("event_id") String eventId,
        @JsonProperty("track_id") String trackId,
        @JsonProperty("sprint_id") String sprintId,
        @JsonProperty("user_id") String userId,
        @JsonProperty("task_id") String taskId,
        Long version,
        @JsonProperty("task_status") TaskStatus taskStatus,
        @JsonProperty("changed_at") LocalDateTime changedAt
) {}
