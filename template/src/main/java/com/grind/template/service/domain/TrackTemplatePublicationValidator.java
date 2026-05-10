package com.grind.template.service.domain;

import org.springframework.stereotype.Component;

@Component
public class TrackTemplatePublicationValidator {
    // TODO Add validateBeforePublish(TrackTemplate template):
    // - required metadata: title, description, durationDays, sprintLength, skillType, category.
    // - at least one task template exists.
    // - task plannedDayOffset values are within template durationDays.
    // - required task fields are present and estimated minutes are positive when provided.
    // - category and tag references are complete and valid.
    // - return a structured validation result or throw a domain-specific exception.
}
