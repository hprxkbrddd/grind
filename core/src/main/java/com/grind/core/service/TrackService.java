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
        track.setName(createTrackRequest.getName());
        track.setDescription(createTrackRequest.getDescription());
        track.setPetId(createTrackRequest.getPetId());
        track.setDurationDays(createTrackRequest.getDurationDays());
        track.setTargetDate(createTrackRequest.getTargetDate());
        track.setMessagePolicy(createTrackRequest.getMessagePolicy());
        track.setStatus(createTrackRequest.getStatus());
        trackRepository.save(track);
        return track;
    }

    public void changeName(ChangeTrackNameRequest changeTrackNameRequest){ trackRepository.changeName(changeTrackNameRequest); }

    public void changeDescription(ChangeTrackDescriptionRequest changeTrackDescriptionRequest){ trackRepository.changeDescription(changeTrackDescriptionRequest); }

    public void changePetId(ChangeTrackPetIdRequest changeTrackPetIdRequest){ trackRepository.changePetId(changeTrackPetIdRequest); }

    public void changePetId(ChangeTrackDurationRequest changeTrackDurationRequest){}

    public void changeTargetDate(ChangeTrackTargetDateRequest changeTrackTargetDateRequest){}

    public void changeMessagePolicy(ChangeTrackMessagePolicyRequest changeTrackMessagePolicyRequest){ trackRepository.changeMessagePolicy(changeTrackMessagePolicyRequest); }

    public void changeStatus(ChangeTrackStatusRequest changeTrackStatusRequest) {}

    public void deleteTask(String id){ trackRepository.deleteTask(id); }
}
