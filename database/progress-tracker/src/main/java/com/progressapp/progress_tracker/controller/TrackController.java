package com.progressapp.progress_tracker.controller;

import com.progressapp.progress_tracker.entity.Track;
import com.progressapp.progress_tracker.entity.User;
import com.progressapp.progress_tracker.repository.TrackRepository;
import com.progressapp.progress_tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tracks")
public class TrackController {

    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private UserRepository userRepository;

    // GET /api/tracks - получить все треки
    @GetMapping
    public List<Track> getAllTracks() {
        return trackRepository.findAll();
    }

    // GET /api/tracks/{id} - получить трек по ID
    @GetMapping("/{id}")
    public Track getTrackById(@PathVariable UUID id) {
        return trackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Track not found with id: " + id));
    }

    // POST /api/tracks - создать новый трек
    @PostMapping
    public Track createTrack(@RequestBody Track track) {
        // Проверяем что пользователь существует
        if (track.getOwner() == null || track.getOwner().getId() == null) {
            throw new RuntimeException("Owner ID is required");
        }

        User owner = userRepository.findById(track.getOwner().getId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + track.getOwner().getId()));
        track.setOwner(owner);

        return trackRepository.save(track);
    }

    // PUT /api/tracks/{id} - обновить трек
    @PutMapping("/{id}")
    public Track updateTrack(@PathVariable UUID id, @RequestBody Track trackDetails) {
        Track track = trackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Track not found with id: " + id));

        track.setName(trackDetails.getName());
        track.setDescription(trackDetails.getDescription());
        track.setDurationDays(trackDetails.getDurationDays());
        track.setTargetDate(trackDetails.getTargetDate());

        // Статус как строка
        if (trackDetails.getStatus() != null) {
            track.setStatus(trackDetails.getStatus());
        }

        return trackRepository.save(track);
    }

    // DELETE /api/tracks/{id} - удалить трек
    @DeleteMapping("/{id}")
    public String deleteTrack(@PathVariable UUID id) {
        Track track = trackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Track not found with id: " + id));

        trackRepository.delete(track);
        return "Track deleted successfully";
    }

    // GET /api/tracks/user/{userId} - получить все треки пользователя
    @GetMapping("/user/{userId}")
    public List<Track> getTracksByUser(@PathVariable UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return trackRepository.findByOwner(user);
    }
}