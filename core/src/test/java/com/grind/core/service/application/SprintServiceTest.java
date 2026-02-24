package com.grind.core.service.application;

import com.grind.core.dto.request.SprintWithCount;
import com.grind.core.exception.InvalidAggregateStateException;
import com.grind.core.model.Sprint;
import com.grind.core.model.Task;
import com.grind.core.model.Track;
import com.grind.core.repository.SprintRepository;
import com.grind.core.repository.TaskRepository;
import com.grind.core.repository.TrackRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SprintServiceTest {

    @Mock
    private SprintRepository sprintRepository;

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private TrackRepository trackRepository;

    @InjectMocks
    private SprintService sprintService;

    // ----- GET BY TRACK -----
    @Test
    void getByTrackId_shouldReturnSprints() {
        SprintWithCount s1 = new SprintWithCount(new Sprint(), 0L, "track-id");
        SprintWithCount s2 = new SprintWithCount(new Sprint(), 0L, "track-id");

        when(sprintRepository.findByTrackIdOrderByStartDateWithCount("t1"))
                .thenReturn(List.of(s1, s2));

        List<SprintWithCount> result = sprintService.getByTrackIdWithCount("t1");

        assertEquals(2, result.size());
        assertEquals(s1, result.get(0));
        assertEquals(s2, result.get(1));
    }

    // ----- CREATE SPRINTS FOR TRACK -----
    @Test
    @SuppressWarnings("unchecked")
    void createSprintsForTrack_shouldSplitCorrectly() {
        Track track = new Track();
        track.setStartDate(LocalDate.of(2024, 1, 1));
        track.setTargetDate(LocalDate.of(2024, 1, 10));
        track.setSprintLength(5);

        ArgumentCaptor<List<Sprint>> captor = ArgumentCaptor.forClass(List.class);
        when(sprintRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

        List<Sprint> result = sprintService.createSprintsForTrack(track);

        verify(sprintRepository).saveAll(captor.capture());
        List<Sprint> saved = captor.getValue();

        assertEquals(2, saved.size());

        assertEquals(LocalDate.of(2024, 1, 1), saved.get(0).getStartDate());
        assertEquals(LocalDate.of(2024, 1, 5), saved.get(0).getEndDate());

        assertEquals(LocalDate.of(2024, 1, 6), saved.get(1).getStartDate());
        assertEquals(LocalDate.of(2024, 1, 10), saved.get(1).getEndDate());

        assertEquals(saved, result);
    }

    @Test
    void createSprintsForTrack_shouldTrimLastSprint() {
        Track track = new Track();
        track.setStartDate(LocalDate.of(2024, 1, 1));
        track.setTargetDate(LocalDate.of(2024, 1, 8));
        track.setSprintLength(5);

        when(sprintRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

        List<Sprint> result = sprintService.createSprintsForTrack(track);

        assertEquals(2, result.size());
        assertEquals(LocalDate.of(2024, 1, 6), result.get(1).getStartDate());
        assertEquals(LocalDate.of(2024, 1, 8), result.get(1).getEndDate());
    }

    // ----- RECREATE SPRINTS FOR TRACK -----
    @Test
    @SuppressWarnings("unchecked")
    void recreateSprintsForTrack_shouldReassignTasksAndDeleteOldSprints() {
        Track track = new Track();
        String trackId = "some-track-uuid";
        String sprintId = "some-sprint-uuid";
        track.setId(trackId);
        track.setStartDate(LocalDate.of(2024, 1, 1));
        track.setTargetDate(LocalDate.of(2024, 1, 10));
        track.setSprintLength(5);

        Sprint oldSprint = new Sprint();
        oldSprint.setId(sprintId);

        when(sprintRepository.findByTrackIdOrderByStartDate(trackId))
                .thenReturn(List.of(oldSprint));

        when(sprintRepository.saveAll(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        Task task1 = new Task();
        task1.setSprint(oldSprint);
        task1.setPlannedDate(LocalDate.of(2024, 1, 2));

        Task task2 = new Task();
        task2.setSprint(oldSprint);
        task2.setPlannedDate(LocalDate.of(2024, 1, 9));

        Task taskOutside = new Task();
        taskOutside.setSprint(oldSprint);
        taskOutside.setPlannedDate(LocalDate.of(2024, 2, 1)); // вне диапазона

        when(taskRepository.findByTrackId(trackId))
                .thenReturn(List.of(task1, task2, taskOutside));

        sprintService.recreateSprintsForTrack(track);

        ArgumentCaptor<List<Task>> taskCaptor = ArgumentCaptor.forClass(List.class);
        verify(taskRepository).saveAll(taskCaptor.capture());
        List<Task> reassigned = taskCaptor.getValue();

        assertEquals(3, reassigned.size());

        Sprint firstSprint = reassigned.get(0).getSprint();
        Sprint secondSprint = reassigned.get(1).getSprint();
        Sprint lastSprint = reassigned.get(2).getSprint();

        assertEquals(LocalDate.of(2024, 1, 1), firstSprint.getStartDate());
        assertEquals(LocalDate.of(2024, 1, 6), secondSprint.getStartDate());

        // вне диапазона — в последний
        assertEquals(lastSprint, reassigned.get(2).getSprint());

        verify(sprintRepository).deleteAll(List.of(oldSprint));
    }

    // ----- FIND SPRINT FOR DATE -----
    @Test
    void findSprintForDate_shouldReturnCorrectSprint() {
        Sprint s1 = new Sprint();
        s1.setStartDate(LocalDate.of(2024, 1, 1));
        s1.setEndDate(LocalDate.of(2024, 1, 5));

        Sprint s2 = new Sprint();
        s2.setStartDate(LocalDate.of(2024, 1, 6));
        s2.setEndDate(LocalDate.of(2024, 1, 10));

        Sprint result = sprintService.findSprintForDate(
                List.of(s1, s2),
                LocalDate.of(2024, 1, 7)
        );


        assertEquals(s2, result);
    }
}
