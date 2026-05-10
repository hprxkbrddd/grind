package com.grind.template.dto.request;

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
