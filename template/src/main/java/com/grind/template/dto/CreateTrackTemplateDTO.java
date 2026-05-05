package com.grind.template.dto;

import com.grind.template.enums.TrackDifficulty;
import com.grind.template.enums.TrackSkillType;
import com.grind.template.enums.TrackVisibility;

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
