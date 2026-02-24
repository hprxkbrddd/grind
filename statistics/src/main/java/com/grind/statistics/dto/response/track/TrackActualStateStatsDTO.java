package com.grind.statistics.dto.response.track;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record TrackActualStateStatsDTO(

        @JsonProperty("track_id") String trackId,

        @JsonProperty("total_tasks") Long totalTasks,

        @JsonProperty("completed_tasks") Long completedTasks,
        @JsonProperty("remaining_tasks") Long remainingTasks,
        @JsonProperty("overdue_tasks") Long overdueTasks,
        @JsonProperty("active_wip") Long activeWip,

        @JsonProperty("completion_percent") BigDecimal completionPercent,
        @JsonProperty("overdue_percent") BigDecimal overduePercent,
        @JsonProperty("overdue_among_active_percent") BigDecimal overdueAmongActivePercent,

        @JsonProperty("avg_active_age_days") Double avgActiveAgeDays
) {
}
