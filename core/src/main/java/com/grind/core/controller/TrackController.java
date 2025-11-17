package com.grind.core.controller;

import com.grind.core.dto.TrackDTO;
import com.grind.core.model.Track;
import com.grind.core.service.TrackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/Track")
public class TrackController {

    private final TrackService trackService;

    public ResponseEntity<Track> createTrack(@RequestBody TrackDTO trackDTO){
        Track track = trackService.createTrack(trackDTO);

        return new ResponseEntity<>(track, HttpStatus.CREATED);
    }

    //public ResponseEntity<Track> deleteTrack(@PathVariable Long id){}
}
