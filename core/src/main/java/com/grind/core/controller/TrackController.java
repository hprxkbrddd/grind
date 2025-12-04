package com.grind.core.controller;

import com.grind.core.dto.TrackDTO;
import com.grind.core.model.Track;
import com.grind.core.request.Track.*;
import com.grind.core.service.TrackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("core/v1/track")
public class TrackController {

    private final TrackService trackService;

    @GetMapping("/get-all")
    public ResponseEntity<List<TrackDTO>> trackIndex(){
        List<Track> tracks = trackService.getAllTracks();
        List<TrackDTO> trackDTOS = tracks.stream().map(Track::mapDTO).collect(Collectors.toList());

        return new ResponseEntity<>(trackDTOS,
                trackDTOS.isEmpty()?
                        HttpStatus.NO_CONTENT : HttpStatus.FOUND);
    }

    @GetMapping("/get-track/{id}")
    public ResponseEntity<TrackDTO> getById(@PathVariable String id){
        return new ResponseEntity<>(trackService.getById(id).mapDTO(), HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<TrackDTO> createTask(@RequestBody CreateTrackRequest createTrackRequest){
        Track track = trackService.createTrack(createTrackRequest);

        return new ResponseEntity<>(track.mapDTO(), HttpStatus.CREATED);
    }

    @PutMapping("/change-name")
    public void changeName(@RequestBody ChangeTrackNameRequest changeTrackNameRequest){
        trackService.changeName(changeTrackNameRequest);
    }

    @PutMapping("/change-description")
    public void changeDescription(@RequestBody ChangeTrackDescriptionRequest changeTrackDescriptionRequest){
        trackService.changeDescription(changeTrackDescriptionRequest);
    }

    @PutMapping("/change-pet")
    public void changePetId(@RequestBody ChangeTrackPetIdRequest changeTrackPetIdRequest){
        trackService.changePetId(changeTrackPetIdRequest);
    }

    @PutMapping("/change-duration")
    public void changeDuration(@RequestBody ChangeTrackDurationRequest changeTrackDurationRequest){
        trackService.changePetId(changeTrackDurationRequest);
    }

    @PutMapping("/change-target-date")
    public void changeTargetDate(@RequestBody ChangeTrackTargetDateRequest changeTrackTargetDateRequest){
        trackService.changeTargetDate(changeTrackTargetDateRequest);
    }

    @PutMapping("/change-message-policy")
    public void changeMessagePolicy(@RequestBody ChangeTrackMessagePolicyRequest changeTrackMessagePolicyRequest){
        trackService.changeMessagePolicy(changeTrackMessagePolicyRequest);
    }

        @PutMapping("/change-status")
    public void changeStatus(@RequestBody ChangeTrackStatusRequest changeTrackStatusRequest){
        trackService.changeStatus(changeTrackStatusRequest);
    }

    @DeleteMapping("/delete-track/{id}")
    public void deleteTask(@PathVariable String id){
        trackService.deleteTask(id);
    }
}
