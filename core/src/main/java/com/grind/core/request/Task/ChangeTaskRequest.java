package com.grind.core.request.Task;

import java.time.LocalDate;

public record ChangeTaskRequest(String title,

        LocalDate plannedDate,

        String description) {
    
}
