package com.grind.gateway.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.grind.gateway.dto.Body;
import com.grind.gateway.dto.core.track.ChangeTrackDTO;
import com.grind.gateway.dto.core.track.CreateTrackRequest;
import com.grind.gateway.service.core.CoreTrackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/core/track")
@RequiredArgsConstructor
public class CoreTrackController {

    private final CoreTrackService trackService;

    @GetMapping
    public ResponseEntity<?> getTracksOfUser() {
        Body body = trackService.callGetTracksOfUser();
        return ResponseEntity.status(body.status()).body(body.payload());
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllTracks() {
        Body body = trackService.callGetAllTracks();
        return ResponseEntity.status(body.status()).body(body.payload());
    }

    @GetMapping("/{trackId}")
    public ResponseEntity<?> getTrack(@PathVariable String trackId) {
        Body body = trackService.callGetTrack(trackId);
        return ResponseEntity.status(body.status()).body(body.payload());
    }

    @GetMapping("/sprints/{trackId}")
    public ResponseEntity<?> getSprintsOfTrack(@PathVariable String trackId) {
        Body body = trackService.callGetSprintsOfTrack(trackId);
        return ResponseEntity.status(body.status()).body(body.payload());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateTrackRequest dto) {
        Body body = trackService.callCreateTrack(dto);
        return ResponseEntity.status(body.status()).body(body.payload());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> changeTrack(@RequestBody ChangeTrackDTO dto, @PathVariable String id) throws JsonProcessingException {
        dto.setId(id);
        Body body = trackService.callChangeTrack(dto);
        return ResponseEntity.status(body.status()).body(body.payload());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        Body body = trackService.callDeleteTrack(id);
        return ResponseEntity.status(body.status()).body(body.payload());
    }
}
