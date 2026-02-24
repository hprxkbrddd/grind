package com.grind.core.dto.request;

import com.grind.core.dto.entity.SprintWithCountDTO;
import com.grind.core.model.Sprint;

public record SprintWithCount(
        Sprint sprint,
        Long tasks,
        String trackId
) {
    public SprintWithCountDTO mapDTO(){
        return new SprintWithCountDTO(
                sprint.getId(),
                sprint.getStartDate(),
                sprint.getEndDate(),
                trackId,
                tasks
        );
    }
}
