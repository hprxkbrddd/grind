package com.grind.gateway.dto.core.task;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@RequiredArgsConstructor
@Getter
public class PlanTaskDateDTO {
    @Setter
    private String taskId;
    private final LocalDate plannedDate;
}
