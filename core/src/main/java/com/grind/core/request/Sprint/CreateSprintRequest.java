package com.grind.core.request.Sprint;

import java.time.LocalDate;

public record CreateSprintRequest(

        LocalDate startDate,

        LocalDate endDate,

        String track_id)  {
}
