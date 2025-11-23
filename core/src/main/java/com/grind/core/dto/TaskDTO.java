package com.grind.core.dto;

public record TaskDTO(
        String id,

        String sprintId,

        String name,

        String description,

        String status
) {
}
