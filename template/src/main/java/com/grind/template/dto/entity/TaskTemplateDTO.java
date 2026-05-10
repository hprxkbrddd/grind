package com.grind.template.dto.entity;

public record TaskTemplateDTO(
        String id,
        String title,
        String description,
        Integer plannedDayOffset
) {
}
