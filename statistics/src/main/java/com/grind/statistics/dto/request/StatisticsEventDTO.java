package com.grind.statistics.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.grind.statistics.enums.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record StatisticsEventDTO(
        @JsonProperty("event_id") Long eventId,
        @JsonProperty("track_id") String trackId,
        @JsonProperty("sprint_id") String sprintId,
        @JsonProperty("user_id") String userId,
        @JsonProperty("task_id") String taskId,
        @JsonProperty("planned_date") LocalDate plannedDate,
        Long version,
        @JsonProperty("task_status") TaskStatus taskStatus,
        @JsonProperty("changed_at") LocalDateTime changedAt
) {}
