package com.grind.core.service.application;

import com.grind.core.dto.request.SprintWithCount;
import com.grind.core.dto.request.track.TrackWithCount;
import com.grind.core.enums.TrackStatus;
import com.grind.core.exception.TaskNotFoundException;
import com.grind.core.exception.TrackNotFoundException;
import com.grind.core.model.Sprint;
import com.grind.core.model.Task;
import com.grind.core.model.Track;
import com.grind.core.repository.TrackRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrackServiceTest {

    @Mock
    TrackRepository trackRepository;
    @Mock
    SprintService sprintService;

    @InjectMocks
    TrackService trackService;

    // ----- GET BY ID -----
    @Test
    void getById_shouldReturnTrack() {
        TrackWithCount track = new TrackWithCount(new Track(), 0L);
        when(trackRepository.findByIdWithTaskCount("t1"))
                .thenReturn(Optional.of(track));

        TrackWithCount result = trackService.getById("t1");

        assertEquals(track, result);
    }

    @Test
    void getById_shouldThrowIfNotFound() {
        when(trackRepository.findByIdWithTaskCount("t1"))
                .thenReturn(Optional.empty());

        assertThrows(TrackNotFoundException.class,
                () -> trackService.getById("t1"));
    }

    // ----- GET SPRINTS OF TRACK -----
    @Test
    void getSprintsOfTrack_shouldDelegate() {
        when(trackRepository.existsById("t1")).thenReturn(true);
        when(sprintService.getByTrackIdWithCount("t1"))
                .thenReturn(List.of(new SprintWithCount(new Sprint(), 0L, "track-id")));

        List<SprintWithCount> result = trackService.getSprintsOfTrackWithCount("t1");

        assertEquals(1, result.size());
        verify(sprintService).getByTrackIdWithCount("t1");
    }

    @Test
    void getSprintsOfTrack_shouldThrowIfTrackNotExists() {
        when(trackRepository.existsById("t1")).thenReturn(false);

        assertThrows(TrackNotFoundException.class,
                () -> trackService.getSprintsOfTrackWithCount("t1"));

        verifyNoInteractions(sprintService);
    }

    // ----- GET BY USER ID -----
    @Test
    void getByUserId_shouldReturnTracks() {
        when(trackRepository.findByUserId("u1"))
                .thenReturn(List.of(new TrackWithCount(new Track(), 0L), new TrackWithCount(new Track(), 0L)));

        List<TrackWithCount> result = trackService.getByUserId("u1");

        assertEquals(2, result.size());
    }

    // ----- CREATE TRACK -----
    @Test
    void createTrack_shouldCreateAndGenerateSprints() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("user1");

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);

        SecurityContextHolder.setContext(context);

        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 1, 10);

        Track result = trackService.createTrack(
                "name",
                "desc",
                "pet1",
                5,
                start,
                end,
                "policy",
                TrackStatus.ACTIVE
        );

        assertEquals("user1", result.getUserId());
        assertEquals(10, result.getDurationDays());
        assertEquals(TrackStatus.ACTIVE, result.getStatus());

        verify(trackRepository).save(result);
        verify(sprintService).createSprintsForTrack(result);
    }

    // ----- DELETE TRACK -----
    @Test
    void deleteTask_shouldDeleteAndReturnTask() {
        Track track = new Track();
        track.setId("t1");

        when(trackRepository.findById("t1"))
                .thenReturn(Optional.of(track));

        Track result = trackService.deleteTrack("t1");

        verify(trackRepository).findById("t1");
        verify(trackRepository).deleteById("t1");

        assertEquals("t1", result.getId());
    }

    @Test
    void deleteTask_shouldThrowIfNotFound() {

        when(trackRepository.findById("t1"))
                .thenReturn(Optional.empty());

        assertThrows(TrackNotFoundException.class,
                () -> trackService.deleteTrack("t1"));

        verify(trackRepository).findById("t1");
        verify(trackRepository, never()).deleteById(any());
    }

    // ----- CHANGE TRACK -----
    @Test
    void changeTrack_shouldRecreateSprintsIfDatesChanged() {
        Track track = new Track();
        track.setStartDate(LocalDate.of(2024, 1, 1));
        track.setTargetDate(LocalDate.of(2024, 1, 10));

        when(trackRepository.findById("t1"))
                .thenReturn(Optional.of(track));

        trackService.changeTrack(
                "t1",
                null,
                null,
                null,
                LocalDate.of(2024, 1, 5),
                LocalDate.of(2024, 1, 15),
                7,
                null,
                null
        );

        verify(sprintService).recreateSprintsForTrack(track);
        assertEquals(11, track.getDurationDays());
    }

    @Test
    void changeTrack_shouldUpdateFieldsWithoutRegen() {
        Track track = new Track();
        when(trackRepository.findById("t1"))
                .thenReturn(Optional.of(track));

        Track result = trackService.changeTrack(
                "t1",
                "newName",
                "newDesc",
                "pet2",
                null,
                null,
                null,
                "policy",
                "ACTIVE"
        );

        assertEquals("newName", result.getName());
        assertEquals("newDesc", result.getDescription());
        assertEquals("pet2", result.getPetId());
        assertEquals("policy", result.getMessagePolicy());
        assertEquals(TrackStatus.ACTIVE, result.getStatus());

        verifyNoInteractions(sprintService);
    }

    @Test
    void changeTrack_shouldThrowIfNotFound() {
        when(trackRepository.findById("t1"))
                .thenReturn(Optional.empty());

        assertThrows(TrackNotFoundException.class,
                () -> trackService.changeTrack("t1", null, null, null, null, null, null, null, null));
    }
}
