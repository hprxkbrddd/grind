package com.grind.core.request.Sprint;

import java.time.LocalDate;

public record CreateSprintRequest(

        String name,

        LocalDate startDate,

        LocalDate endDate,

        String status,

        String track_id)  {
}
