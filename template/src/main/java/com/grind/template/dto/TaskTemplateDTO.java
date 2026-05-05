package com.grind.template.dto;

public record TaskTemplateDTO(
        String id,
        String title,
        String description,
        Integer plannedDayOffset
) {
}
