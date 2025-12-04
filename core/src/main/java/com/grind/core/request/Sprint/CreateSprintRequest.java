package com.grind.core.request.Sprint;

import com.grind.core.model.Track;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class CreateSprintRequest {

    private String name;  // "Спринт 1", "Неделя 1", "Этап 1"

    private LocalDate startDate;

    private LocalDate endDate;

    private String status;

    private LocalDateTime createdAt;

    private Track track;
}
