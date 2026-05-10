package com.grind.template.dto.request;

import com.grind.template.enums.TrackDifficulty;
import com.grind.template.enums.TrackSkillType;
import com.grind.template.enums.TrackVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record EditTrackTemplateDTO(
        @NotBlank(message = "Track template id must not be null or blank")
        String id,
        TrackVisibility visibility,
        @Pattern(
                regexp = ".*\\S.*",
                flags = Pattern.Flag.DOTALL,
                message = "Title must not be blank"
        )
        String title,
        @Pattern(
                regexp = ".*\\S.*",
                flags = Pattern.Flag.DOTALL,
                message = "Description must not be blank"
        )
        String description,
        @Pattern(
                regexp = ".*\\S.*",
                flags = Pattern.Flag.DOTALL,
                message = "Expected result must not be blank"
        )
        String expectedResult,
        @Positive(message = "Duration days must be positive")
        Integer durationDays,
        @Positive(message = "Sprint length must be positive")
        Integer sprintLength,
        @Positive(message = "Estimated time per day minutes must be positive")
        Integer estimatedTimePerDayMinutes,
        TrackDifficulty difficulty,
        TrackSkillType skillType,
        List<
                @NotBlank(message = "Tag slug must not be null or blank")
                String
                > tags,
        @Pattern(
                regexp = ".*\\S.*",
                flags = Pattern.Flag.DOTALL,
                message = "Category must not be blank"
        )
        String category
) {
}
