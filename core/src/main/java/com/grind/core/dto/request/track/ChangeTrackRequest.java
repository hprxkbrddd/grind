package com.grind.core.dto.request.track;

import java.time.LocalDate;

public record ChangeTrackRequest(
        String id,
        String name,
        String description,
        String petId,
        LocalDate startDate,
        LocalDate targetDate,
        Integer sprintLength,
        String messagePolicy,
        String status) {

}
