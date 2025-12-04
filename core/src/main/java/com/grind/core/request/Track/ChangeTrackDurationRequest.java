package com.grind.core.request.Track;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ChangeTrackDurationRequest {
    private String id;

    private Integer durationDays;
}
