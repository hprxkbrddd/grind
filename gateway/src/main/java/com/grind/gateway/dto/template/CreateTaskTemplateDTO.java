package com.grind.gateway.dto.template;

public record CreateTaskTemplateDTO(
        String title,
        String description,
        String trackTemplateId,
        Integer plannedDayOffset,
        Boolean isRequired,
        String taskGroup,
        Integer estimatedMinutes
) {
}
