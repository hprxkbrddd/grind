package com.grind.core.request.Task;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CreateTaskRequest {

    private String sprintId;

    private String name;

    private String description;

    private String status;
}
