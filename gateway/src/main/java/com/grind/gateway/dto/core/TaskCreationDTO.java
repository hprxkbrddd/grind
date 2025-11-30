package com.grind.gateway.dto.core;

import java.time.LocalDate;

public record TaskCreationDTO(
        String title,
        String description,
        LocalDate plannedDate
) {
}
