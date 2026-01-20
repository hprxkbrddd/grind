package com.grind.core.request.Task;

import java.time.LocalDate;

public record CreateTaskRequest(

        String title,

        String sprint_id,

        LocalDate plannedDate,

        String description,

        String status) {
}
