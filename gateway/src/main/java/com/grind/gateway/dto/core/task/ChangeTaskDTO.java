package com.grind.gateway.dto.core.task;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
public class ChangeTaskDTO {
    @Setter
    private String taskId;
    private final String title;
    private final String description;
}
