package com.grind.gateway.dto.statistics;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Track statistics calculated from the actual task state")
public record TrackActualStateStatsDTO(
        @JsonProperty("track_id")
        @Schema(description = "Track identifier", example = "track-1")
        String trackId,

        @JsonProperty("total_tasks")
        @Schema(description = "Total number of visible tasks", example = "42")
        Long totalTasks,

        @JsonProperty("completed_tasks")
        @Schema(description = "Number of completed tasks", example = "18")
        Long completedTasks,

        @JsonProperty("remaining_tasks")
        @Schema(description = "Number of remaining tasks", example = "24")
        Long remainingTasks,

        @JsonProperty("overdue_tasks")
        @Schema(description = "Number of overdue tasks", example = "5")
        Long overdueTasks,

        @JsonProperty("active_wip")
        @Schema(description = "Number of active planned tasks", example = "19")
        Long activeWip,

        @JsonProperty("completion_percent")
        @Schema(description = "Completion percentage", example = "42.86")
        BigDecimal completionPercent,

        @JsonProperty("overdue_percent")
        @Schema(description = "Percentage of overdue tasks among all tasks", example = "11.90")
        BigDecimal overduePercent,

        @JsonProperty("overdue_among_active_percent")
        @Schema(description = "Percentage of overdue tasks among non-completed tasks", example = "20.83")
        BigDecimal overdueAmongActivePercent,

        @JsonProperty("avg_active_age_days")
        @Schema(description = "Average age of active tasks in days", example = "6.5")
        Double avgActiveAgeDays
) {
}
