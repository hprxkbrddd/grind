package com.grind.template.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record EditTaskTemplateDTO(
        @NotBlank(message = "Task template id must not be null or blank")
        String id,
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
                message = "Track template id must not be blank"
        )
        String trackTemplateId,
        @PositiveOrZero(message = "Planned day offset must be non-negative")
        Integer plannedDayOffset,
        Boolean isRequired,
        @Pattern(
                regexp = ".*\\S.*",
                flags = Pattern.Flag.DOTALL,
                message = "Task group must not be blank"
        )
        String taskGroup,
        @Positive(message = "Estimated minutes must be positive")
        Integer estimatedMinutes
) {
}
