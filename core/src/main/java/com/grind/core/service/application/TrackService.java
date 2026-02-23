package com.grind.core.service.application;

import com.grind.core.enums.TrackStatus;
import com.grind.core.exception.TrackNotFoundException;
import com.grind.core.model.Sprint;
import com.grind.core.model.Track;
import com.grind.core.dto.request.track.TrackWithCount;
import com.grind.core.repository.TrackRepository;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrackService {

    private final TrackRepository trackRepository;
    private final SprintService sprintService;

    @PreAuthorize("hasRole('ADMIN')")
    public List<TrackWithCount> getAllTracksWithCount() {
        return trackRepository.findAllWithTaskCount();
    }

    public TrackWithCount getById(
            @NotBlank(message = "Track id must be not null or blank")
            String id
    ) {
        return trackRepository.findByIdWithTaskCount(id)
                .orElseThrow(() -> new TrackNotFoundException(id));
    }

    public List<Sprint> getSprintsOfTrack(
            @NotBlank(message = "Track id must be not null or blank")
            String trackId
    ) {
        if (!trackRepository.existsById(trackId))
            throw new TrackNotFoundException(trackId);
        return sprintService.getByTrackId(trackId);
    }

    public List<TrackWithCount> getByUserId(
            @NotBlank(message = "User id must be not null or blank")
            String userId
    ) {
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
            TrackStatus status
    ) {
        Track track = new Track();

        track.setUserId(SecurityContextHolder.getContext().getAuthentication().getName());
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
    public Track deleteTrack(String id) {
        Track track = trackRepository.findById(id)
                        .orElseThrow(() -> new TrackNotFoundException(id));
        trackRepository.deleteById(id);
        return track;
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
                .orElseThrow(() -> new TrackNotFoundException(id));

        boolean regen = false;

        if (startDate != null) {
            track.setStartDate(startDate);
            regen = true;
        }
        if (targetDate != null) {
            track.setTargetDate(targetDate);
            regen = true;
        }
        if (sprintLength != null) {
            track.setSprintLength(sprintLength);
            regen = true;
        }

        if (regen) {
            LocalDate s = track.getStartDate();
            LocalDate t = track.getTargetDate();

            track.setDurationDays(Math.toIntExact(ChronoUnit.DAYS.between(s, t) + 1));

            sprintService.recreateSprintsForTrack(track);
        }

        if (name != null) track.setName(name);
        if (description != null) track.setDescription(description);
        if (petId != null) track.setPetId(petId);
        if (messagePolicy != null) track.setMessagePolicy(messagePolicy);
        if (status != null) track.setStatus(TrackStatus.valueOf(status));
        return track;
    }
}
