package com.grind.gateway.dto.template;

import com.grind.gateway.enums.TrackDifficulty;
import com.grind.gateway.enums.TrackSkillType;
import com.grind.gateway.enums.TrackVisibility;

import java.util.List;

public record CreateTrackTemplateDTO(
        String authorId,
        TrackVisibility visibility,
        String title,
        String description,
        String expectedResult,
        Integer durationDays,
        Integer sprintLength,
        Integer estimatedTimePerDayMinutes,
        TrackDifficulty difficulty,
        TrackSkillType skillType,
        List<String> tags,
        String category
) {
}
