package com.grind.core.dto.request.track;

import com.grind.core.dto.entity.TrackWithCountDTO;
import com.grind.core.model.Track;

public record TrackWithCount(
        Track track,
        Long tasks
) {
    public TrackWithCountDTO mapDTO(){
        return new TrackWithCountDTO(
                track.getId(),
                track.getName(),
                track.getDescription(),
                track.getPetId(),
                track.getDurationDays(),
                tasks,
                track.getStartDate(),
                track.getTargetDate(),
                track.getCreatedAt(),
                track.getMessagePolicy(),
                track.getStatus(),
                track.getUserId()
        );
    }
}
