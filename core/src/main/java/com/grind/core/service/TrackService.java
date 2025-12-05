package com.grind.core.service;

import com.grind.core.model.Track;
import com.grind.core.repository.TrackRepository;
import com.grind.core.request.Track.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrackService {

    private final TrackRepository trackRepository;

    public List<Track> getAllTracks(){ return trackRepository.getAllTracks(); }

    public Track getById(String id){ return trackRepository.getById(id); }

    public Track createTrack(CreateTrackRequest createTrackRequest){
        Track track = new Track();
        track.setName(createTrackRequest.name());
        track.setDescription(createTrackRequest.description());
        track.setPetId(createTrackRequest.petId());
        track.setDurationDays(createTrackRequest.durationDays());
        track.setTargetDate(createTrackRequest.targetDate());
        track.setMessagePolicy(createTrackRequest.messagePolicy());
        track.setStatus(createTrackRequest.status());
        trackRepository.save(track);
        return track;
    }

    public void deleteTask(String id){ trackRepository.deleteTask(id); }

    public void changeTrack(ChangeTrackRequest changeTrackRequest){}
}
