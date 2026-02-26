package com.grind.gateway.dto.core.task;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Schema(description = "Plan task by date")
@RequiredArgsConstructor
@Getter
public class PlanTaskDateDTO {
    @Schema(description = "Task identifier from path", accessMode = Schema.AccessMode.READ_ONLY)
    @Setter
    private String taskId;
    @Schema(description = "Planned date")
    private final LocalDate plannedDate;
}
