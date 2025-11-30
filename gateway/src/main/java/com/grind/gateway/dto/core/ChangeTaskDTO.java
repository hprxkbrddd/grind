package com.grind.gateway.dto.core;

import com.grind.gateway.enums.TaskStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@RequiredArgsConstructor
@Getter
public class ChangeTaskDTO {
    @Setter
    private String id;
    private final String title;
    private final String description;
    private final LocalDate plannedDate;
    private final LocalDate actualDate;
    private final TaskStatus status;
    private final String sprintId;
}
