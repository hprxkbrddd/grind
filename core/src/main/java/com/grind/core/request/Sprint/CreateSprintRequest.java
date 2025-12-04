package com.grind.core.request.Sprint;

import com.grind.core.model.Track;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Setter
public class CreateSprintRequest {

    private String name;

    private LocalDate startDate;

    private LocalDate endDate;

    private String status;

    private Track track;
}
