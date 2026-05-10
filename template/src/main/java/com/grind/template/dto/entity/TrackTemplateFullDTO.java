package com.grind.template.dto.entity;

import com.grind.template.enums.TrackDifficulty;
import com.grind.template.enums.TrackSkillType;
import com.grind.template.enums.TrackTemplateStatus;
import com.grind.template.enums.TrackVisibility;

import java.time.LocalDateTime;
import java.util.Set;

public record TrackTemplateFullDTO(
        TrackTemplateDTO track,
        String authorId,
        Integer estimatedTimePerDayMinutes,
        String expectedResult,
        TrackDifficulty difficulty,
        TrackSkillType skillType,
        String category,
        Set<String> tags,
        TrackVisibility visibility,
        TrackTemplateStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime publishedAt,
        Integer version
) {
}
