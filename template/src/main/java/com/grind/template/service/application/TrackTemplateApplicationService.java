package com.grind.template.service.application;

import org.springframework.stereotype.Service;

@Service
public class TrackTemplateApplicationService {
    // TODO Add applyTemplate(String templateId, String userId, TaskPlanningMode planningMode):
    // - load the template and verify it can be used by the user.
    // - require template status PUBLISHED unless internal/admin rules allow drafts.
    // - apply visibility rules: PRIVATE, UNLISTED, PUBLIC.
    // - select task templates according to planningMode: BACKLOG, PARTIAL, FULL.
    // - build an immutable application snapshot with template metadata and selected tasks.
    // - record TemplateUsage with the applied template version/revision.
    // - return a domain result/command that can later be consumed by track/task creation logic.
    //
    // TODO Keep this service independent from other microservices:
    // - do not directly create track/task records in external services here.
    // - expose enough structured data for an integration layer to create the concrete user track.
}
