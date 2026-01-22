package com.grind.core.service;

import com.grind.core.dto.TrackDTO;
import com.grind.core.model.Track;
import com.grind.core.repository.TrackRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrackService {

    private final TrackRepository trackRepository;
    private final SprintService sprintService;

    @PreAuthorize("hasRole('ADMIN')")
    public List<TrackDTO> getAllTracks() {
        return trackRepository.findAll()
                .stream().map(Track::mapDTO)
                .toList();
    }

    public Track getById(String id) {
        return trackRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("could not find track with this id"));
    }

    public List<Track> getByUserId(String userId){
        return trackRepository.findByUserId(userId);
    }

    @Transactional
    public Track createTrack(
            String name,
            String description,
            String petId,
            Integer sprintLength,
            LocalDate startDate,
            LocalDate targetDate,
            String messagePolicy,
            String status
    ) {
        Track track = new Track();

        track.setName(name);
        track.setDescription(description);
        track.setPetId(petId);
        track.setDurationDays(Math.toIntExact(ChronoUnit.DAYS.between(startDate, targetDate) + 1));
        track.setStartDate(startDate);
        track.setTargetDate(targetDate);
        track.setMessagePolicy(messagePolicy);
        track.setStatus(status);
        track.setSprintLength(sprintLength);

        trackRepository.save(track);
        sprintService.createSprintsForTrack(track);

        return track;
    }

    @Transactional
    public String deleteTrack(String id) {
        trackRepository.deleteById(id);
        return id;
    }

    @Transactional
    public Track changeTrack(
            String id,
            String name,
            String description,
            String petId,
            LocalDate startDate,
            LocalDate targetDate,
            Integer sprintLength,
            String messagePolicy,
            String status
    ) {
        Track track = trackRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Could not find track with id:"+id));

        boolean regen = false;

        if (startDate != null) { track.setStartDate(startDate); regen = true; }
        if (targetDate != null) { track.setTargetDate(targetDate); regen = true; }
        if (sprintLength != null) { track.setSprintLength(sprintLength); regen = true; }

        if (regen) {
            LocalDate s = track.getStartDate();
            LocalDate t = track.getTargetDate();

            track.setDurationDays(Math.toIntExact(ChronoUnit.DAYS.between(s, t) + 1));

            sprintService.recreateSprintsForTrack(track);
        }

        if (name!=null) track.setName(name);
        if (description!=null) track.setDescription(description);
        if (petId!=null) track.setPetId(petId);
        if (messagePolicy!=null) track.setMessagePolicy(messagePolicy);
        if (status!=null) track.setStatus(status);
        return track;
    }
}
