package com.grind.statistics.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public record CoreRecord(
        @JsonProperty("event_id") String eventId,
        @JsonProperty("track_id") String trackId,
        @JsonProperty("sprint_id") String sprintId,
        @JsonProperty("task_id") String taskId,
        Integer version,
        @JsonProperty("task_status") TaskStatus taskStatus,
        @JsonProperty("changed_at") LocalDate changedAt
) {}
