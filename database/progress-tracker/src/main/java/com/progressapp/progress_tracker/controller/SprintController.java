package com.progressapp.progress_tracker.controller;

import com.progressapp.progress_tracker.entity.Sprint;
import com.progressapp.progress_tracker.entity.Track;
import com.progressapp.progress_tracker.repository.SprintRepository;
import com.progressapp.progress_tracker.repository.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/sprints")
public class SprintController {

    @Autowired
    private SprintRepository sprintRepository;

    @Autowired
    private TrackRepository trackRepository;

    // GET /api/sprints - получить все спринты
    @GetMapping
    public List<Sprint> getAllSprints() {
        return sprintRepository.findAll();
    }

    // GET /api/sprints/{id} - получить спринт по ID
    @GetMapping("/{id}")
    public Sprint getSprintById(@PathVariable UUID id) {
        return sprintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sprint not found with id: " + id));
    }

    // POST /api/sprints - создать новый спринт
    @PostMapping
    public Sprint createSprint(@RequestBody Sprint sprint) {
        // Проверяем что трек существует
        Track track = trackRepository.findById(sprint.getTrack().getId())
                .orElseThrow(() -> new RuntimeException("Track not found with id: " + sprint.getTrack().getId()));
        sprint.setTrack(track);

        return sprintRepository.save(sprint);
    }

    // PUT /api/sprints/{id} - обновить спринт
    @PutMapping("/{id}")
    public Sprint updateSprint(@PathVariable UUID id, @RequestBody Sprint sprintDetails) {
        Sprint sprint = sprintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sprint not found with id: " + id));

        sprint.setName(sprintDetails.getName());
        sprint.setStartDate(sprintDetails.getStartDate());
        sprint.setEndDate(sprintDetails.getEndDate());
        sprint.setDurationDays(sprintDetails.getDurationDays());
        sprint.setStatus(sprintDetails.getStatus());

        return sprintRepository.save(sprint);
    }

    // DELETE /api/sprints/{id} - удалить спринт
    @DeleteMapping("/{id}")
    public String deleteSprint(@PathVariable UUID id) {
        Sprint sprint = sprintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sprint not found with id: " + id));

        sprintRepository.delete(sprint);
        return "Sprint deleted successfully";
    }

    // GET /api/sprints/track/{trackId} - получить все спринты трека
    @GetMapping("/track/{trackId}")
    public List<Sprint> getSprintsByTrack(@PathVariable UUID trackId) {
        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new RuntimeException("Track not found with id: " + trackId));
        return sprintRepository.findByTrack(track);
    }
}