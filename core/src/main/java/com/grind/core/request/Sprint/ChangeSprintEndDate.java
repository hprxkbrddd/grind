package com.grind.core.request.Sprint;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ChangeSprintEndDate {
    private String id;

    private LocalDate endDate;
}
