package com.grind.core.request.Sprint;

import java.time.LocalDate;

public record ChangeSprintRequest( String name,

        LocalDate startDate,

        LocalDate endDate,

        String status) {
}
