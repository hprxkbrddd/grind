package com.grind.gateway.dto.core.track;

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
    private final String petId;
    private final LocalDate startDate;
    private final LocalDate targetDate;
    private final Integer sprintLength;
    private final String messagePolicy;
    private final TrackStatus status;
}
