package com.grind.template.dto;

import java.util.Set;

public record TrackTemplateDTO(
        String id,
        String title,
        String description,
        Integer durationDays,
        Integer sprintLength,
        Set<TaskTemplateDTO> taskTemplates
) {
}
