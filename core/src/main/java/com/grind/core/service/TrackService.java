package com.grind.core.service;

import com.grind.core.dto.TrackDTO;
import com.grind.core.model.Track;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrackService {

    public Track createTrack(TrackDTO trackDTO){
        Track track = new Track();
        track.setName(trackDTO.getName());
        track.setDescription(trackDTO.getDescription());
        track.setPetId(track.getPetId());
        track.setMessagePolicy(track.getMessagePolicy());
        return track;
    }
}
