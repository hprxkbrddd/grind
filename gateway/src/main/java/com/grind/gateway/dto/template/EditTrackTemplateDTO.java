package com.grind.gateway.dto.template;

import com.grind.gateway.enums.TrackDifficulty;
import com.grind.gateway.enums.TrackSkillType;
import com.grind.gateway.enums.TrackVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class EditTrackTemplateDTO {
    @NotBlank(message = "Track template id must not be null or blank")
    @Setter
    private String id;
    private final TrackVisibility visibility;
    @Pattern(
            regexp = ".*\\S.*",
            flags = Pattern.Flag.DOTALL,
            message = "Title must not be blank"
    )
    private final String title;
    @Pattern(
            regexp = ".*\\S.*",
            flags = Pattern.Flag.DOTALL,
            message = "Description must not be blank"
    )
    private final String description;
    @Pattern(
            regexp = ".*\\S.*",
            flags = Pattern.Flag.DOTALL,
            message = "Expected result must not be blank"
    )
    private final String expectedResult;
    @Positive(message = "Duration days must be positive")
    private final Integer durationDays;
    @Positive(message = "Sprint length must be positive")
    private final Integer sprintLength;
    @Positive(message = "Estimated time per day minutes must be positive")
    private final Integer estimatedTimePerDayMinutes;
    private final TrackDifficulty difficulty;
    private final TrackSkillType skillType;
    private final List<
            @NotBlank(message = "Tag slug must not be null or blank")
            String
            > tags;
    @Pattern(
            regexp = ".*\\S.*",
            flags = Pattern.Flag.DOTALL,
            message = "Category must not be blank"
    )
    private final String category;
}
