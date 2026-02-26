package com.grind.gateway.dto.core.task;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Schema(description = "Plan task into sprint")
@RequiredArgsConstructor
@Getter
public class PlanTaskSprintDTO{
    @Schema(description = "Task identifier from path", accessMode = Schema.AccessMode.READ_ONLY)
    @Setter
    private String taskId;
    @Schema(description = "Sprint identifier")
    private final String sprintId;
    @Schema(description = "Day of sprint")
    private final Integer dayOfSprint;
}
