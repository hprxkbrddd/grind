package com.grind.core.dto;

public record TrackDTO(
        Long id,

        String name,

        String description,

        String petId,

        String messagePolicy) {
}
