package com.grind.core.request.Task;

import java.time.LocalDate;

import javax.swing.Spring;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ChangeTaskPlannedDate {
    private Spring id;

    private LocalDate plannedDate;
}
