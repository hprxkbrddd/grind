package com.grind.template.dto;

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
