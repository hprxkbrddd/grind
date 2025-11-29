package com.grind.gateway.dto;

import java.time.LocalDate;

public record TaskCreationDTO(
        String title,
        String description,
        LocalDate plannedDate
) {
}
