package com.grind.gateway.dto.statistics;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Sprint statistics calculated from the actual task state")
public record SprintStatsDTO(
        @JsonProperty("sprint_id")
        @Schema(description = "Sprint identifier", example = "sprint-1")
        String sprintId,

        @JsonProperty("total_tasks")
        @Schema(description = "Total number of visible tasks", example = "20")
        Long totalTasks,

        @JsonProperty("completed_tasks")
        @Schema(description = "Number of completed tasks", example = "9")
        Long completedTasks,

        @JsonProperty("remaining_tasks")
        @Schema(description = "Number of remaining tasks", example = "11")
        Long remainingTasks,

        @JsonProperty("overdue_tasks")
        @Schema(description = "Number of overdue tasks", example = "3")
        Long overdueTasks,

        @JsonProperty("active_wip")
        @Schema(description = "Number of active planned tasks", example = "8")
        Long activeWip,

        @JsonProperty("completion_percent")
        @Schema(description = "Completion percentage", example = "45.00")
        BigDecimal completionPercent,

        @JsonProperty("overdue_percent")
        @Schema(description = "Percentage of overdue tasks among all tasks", example = "15.00")
        BigDecimal overduePercent,

        @JsonProperty("overdue_among_active_percent")
        @Schema(description = "Percentage of overdue tasks among non-completed tasks", example = "27.27")
        BigDecimal overdueAmongActivePercent,

        @JsonProperty("avg_active_age_days")
        @Schema(description = "Average age of active tasks in days", example = "4.2")
        Double avgActiveAgeDays
) {
}
