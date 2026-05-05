package com.grind.template.service.application;

import org.springframework.stereotype.Service;

@Service
public class TemplateUsageService {
    // TODO This service should record and read usage facts only.
    // TODO Put the actual applyTemplate(...) use case in TrackTemplateApplicationService.

    // TODO Add recordUsage(String templateId, String userId):
    // - verify the template can be used by the user.
    // - store the template version/revision used.
    // - optionally store the created track id and selected planning mode.
    //
    // TODO Add canUseTemplate(String templateId, String userId):
    // - enforce status and visibility rules.
    // - reject archived or invalid templates.
    //
    // TODO Add usage read methods:
    // - getUsageStats(String templateId).
    // - getUserUsageHistory(String userId).
}
