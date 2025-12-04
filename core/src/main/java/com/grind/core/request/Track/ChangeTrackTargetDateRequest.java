package com.grind.core.request.Track;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ChangeTrackTargetDateRequest {
    private String id;

    private LocalDate targetDate;
}
