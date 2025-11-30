package com.grind.gateway.dto.core;

import com.grind.gateway.enums.TrackStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@RequiredArgsConstructor
@Getter
public class ChangeTrackDTO {
    @Setter
    private String id;
    private final String name;
    private final String description;
    private final Integer durationDays;
    private final LocalDate targetDate;
    private final TrackStatus status;
}
