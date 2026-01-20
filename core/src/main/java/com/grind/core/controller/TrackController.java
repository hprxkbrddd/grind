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

    @GetMapping
    public ResponseEntity<List<TrackDTO>> trackIndex(){
        List<Track> tracks = trackService.getAllTracks();
        List<TrackDTO> trackDTOS = tracks.stream().map(Track::mapDTO).collect(Collectors.toList());

        return new ResponseEntity<>(trackDTOS,
                trackDTOS.isEmpty()?
                        HttpStatus.NO_CONTENT : HttpStatus.FOUND);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrackDTO> getById(@PathVariable String id){
        return new ResponseEntity<>(trackService.getById(id).mapDTO(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<TrackDTO> createTask(@RequestBody CreateTrackRequest createTrackRequest){
        Track track = trackService.createTrack(createTrackRequest);

        return new ResponseEntity<>(track.mapDTO(), HttpStatus.CREATED);
    }

    @PutMapping("/change")
    public void changeTrack(@RequestBody ChangeTrackRequest changeTrackRequest){
        trackService.changeTrack(changeTrackRequest);
    }

    @DeleteMapping("/delete-track/{id}")
    public void deleteTask(@PathVariable String id){
        trackService.deleteTask(id);
    }
}
