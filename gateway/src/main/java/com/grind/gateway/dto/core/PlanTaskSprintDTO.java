package com.grind.gateway.dto.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
public class PlanTaskSprintDTO{
    @Setter
    private String taskId;
    private final String sprintId;
    private final Integer dayOfSprint;
}
