package com.grind.core.service.application;

import com.grind.core.enums.TaskStatus;
import com.grind.core.exception.SprintNotFoundException;
import com.grind.core.exception.TaskNotFoundException;
import com.grind.core.exception.TrackNotFoundException;
import com.grind.core.model.Sprint;
import com.grind.core.model.Task;
import com.grind.core.model.Track;
import com.grind.core.repository.SprintRepository;
import com.grind.core.repository.TaskRepository;
import com.grind.core.repository.TrackRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    TaskRepository taskRepository;
    @Mock
    SprintRepository sprintRepository;
    @Mock
    TrackRepository trackRepository;

    @InjectMocks
    TaskService taskService;

    // ----- GET ALL -----
    @Test
    void getAllTasks_shouldReturnAll() {
        List<Task> tasks = List.of(new Task(), new Task());
        when(taskRepository.findAll()).thenReturn(tasks);

        List<Task> result = taskService.getAllTasks();

        assertEquals(2, result.size());
        verify(taskRepository).findAll();
    }

    // ----- GET BY ID -----
    @Test
    void getById_shouldReturnTask() {
        Task task = new Task();
        when(taskRepository.findById("1")).thenReturn(Optional.of(task));

        Task result = taskService.getById("1");

        assertEquals(task, result);
    }

    @Test
    void getById_shouldThrowIfNotFound() {
        when(taskRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class,
                () -> taskService.getById("1"));
    }

    // ----- GET BY SPRINT -----
    @Test
    void getBySprint_shouldReturnTasks() {
        when(sprintRepository.existsById("s1")).thenReturn(true);
        when(taskRepository.findBySprintId("s1"))
                .thenReturn(List.of(new Task()));

        List<Task> result = taskService.getBySprint("s1");

        assertEquals(1, result.size());
    }

    @Test
    void getBySprint_shouldThrowIfSprintNotExists() {
        when(sprintRepository.existsById("s1")).thenReturn(false);

        assertThrows(SprintNotFoundException.class,
                () -> taskService.getBySprint("s1"));
    }

    // ----- CREATE TASK -----
    @Test
    void createTask_shouldCreate() {
        Track track = new Track();
        when(trackRepository.findById("t1"))
                .thenReturn(Optional.of(track));

        Task result = taskService.createTask("title", "t1", "desc");

        assertEquals(TaskStatus.CREATED, result.getStatus());
        assertEquals(track, result.getTrack());

        verify(taskRepository).save(result);
    }

    @Test
    void createTask_shouldThrowIfTrackNotFound() {
        when(trackRepository.findById("t1"))
                .thenReturn(Optional.empty());

        assertThrows(TrackNotFoundException.class,
                () -> taskService.createTask("title", "t1", "desc"));
    }

    // ----- PLAN TASK SPRINT -----
    @Test
    void planTaskSprint_shouldPlan() {
        Track track = new Track();
        track.setId("t1");

        Sprint sprint = new Sprint();
        sprint.setStartDate(LocalDate.of(2024, 1, 1));
        sprint.setEndDate(LocalDate.of(2024, 1, 5));
        sprint.setTrack(track);

        Task task = new Task();
        task.setTrack(track);

        when(sprintRepository.findById("s1")).thenReturn(Optional.of(sprint));
        when(taskRepository.findById("task1")).thenReturn(Optional.of(task));

        Task result = taskService.planTaskSprint("task1", "s1", 2);

        assertEquals(LocalDate.of(2024, 1, 3), result.getPlannedDate());
        assertEquals(TaskStatus.PLANNED, result.getStatus());
    }

    @Test
    void planTaskSprint_shouldThrowIfDayOutOfRange() {
        Sprint sprint = new Sprint();
        sprint.setStartDate(LocalDate.of(2024, 1, 1));
        sprint.setEndDate(LocalDate.of(2024, 1, 5));

        when(sprintRepository.findById("s1"))
                .thenReturn(Optional.of(sprint));

        assertThrows(IllegalArgumentException.class,
                () -> taskService.planTaskSprint("t1", "s1", 5));
    }

    @Test
    void planTaskSprint_shouldThrowIfDifferentTrack() {
        Track track1 = new Track();
        track1.setId("t1");
        Track track2 = new Track();
        track2.setId("t2");

        Sprint sprint = new Sprint();
        sprint.setStartDate(LocalDate.of(2024, 1, 1));
        sprint.setEndDate(LocalDate.of(2024, 1, 5));
        sprint.setTrack(track2);

        Task task = new Task();
        task.setTrack(track1);

        when(sprintRepository.findById("s1")).thenReturn(Optional.of(sprint));
        when(taskRepository.findById("task1")).thenReturn(Optional.of(task));

        assertThrows(IllegalArgumentException.class,
                () -> taskService.planTaskSprint("task1", "s1", 1));
    }

    // ----- COMPLETE TASK -----
    @Test
    void completeTask_shouldMarkCompleted() {
        Task task = new Task();
        when(taskRepository.findById("t1"))
                .thenReturn(Optional.of(task));

        Task result = taskService.completeTask("t1");

        assertEquals(TaskStatus.COMPLETED, result.getStatus());
        assertNotNull(result.getActualDate());
    }

    // ----- TASK TO BACKLOG -----
    @Test
    void toBackLog_shouldResetFieldsForPlanned() {
        Task task = new Task();
        task.setStatus(TaskStatus.PLANNED);
        task.setPlannedDate(LocalDate.now());
        task.setSprint(new Sprint());

        when(taskRepository.findById("t1"))
                .thenReturn(Optional.of(task));

        Task result = taskService.toBackLog("t1");

        assertNull(result.getPlannedDate());
        assertNull(result.getSprint());
        assertEquals(TaskStatus.CREATED, result.getStatus());
    }

    @Test
    void toBackLog_shouldClearActualDateIfCompleted() {
        Task task = new Task();
        task.setStatus(TaskStatus.COMPLETED);
        task.setActualDate(LocalDate.now());
        task.setPlannedDate(LocalDate.now());
        task.setSprint(new Sprint());

        when(taskRepository.findById("t1"))
                .thenReturn(Optional.of(task));

        Task result = taskService.toBackLog("t1");

        assertNull(result.getActualDate());
        assertNull(result.getPlannedDate());
        assertNull(result.getSprint());
        assertEquals(TaskStatus.CREATED, result.getStatus());
    }

    @Test
    void toBackLog_shouldReturnSameIfAlreadyCreated() {
        Task task = new Task();
        task.setStatus(TaskStatus.CREATED);
        task.setPlannedDate(LocalDate.now());
        task.setActualDate(LocalDate.now());
        task.setSprint(new Sprint());

        when(taskRepository.findById("t1"))
                .thenReturn(Optional.of(task));

        Task result = taskService.toBackLog("t1");

        assertEquals(TaskStatus.CREATED, result.getStatus());
        assertNotNull(result.getPlannedDate());
        assertNotNull(result.getActualDate());
        assertNotNull(result.getSprint());
    }

    @Test
    void toBackLog_shouldThrowIfNotFound() {
        when(taskRepository.findById("t1"))
                .thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class,
                () -> taskService.toBackLog("t1"));
    }

    // ----- OVERDUE TASK -----
    @Test
    void markOverdue_shouldMarkAll() {
        Task t = new Task();
        t.setStatus(TaskStatus.PLANNED);

        when(taskRepository.getOverdueWithStatus(any(), eq(TaskStatus.PLANNED)))
                .thenReturn(List.of(t));

        List<Task> result = taskService.markOverdue();

        assertEquals(TaskStatus.OVERDUE, result.get(0).getStatus());
    }

    // ----- DELETE TASK -----
    @Test
    void deleteTask_shouldCallRepository() {
        String id = taskService.deleteTask("t1");

        verify(taskRepository).deleteById("t1");
        assertEquals("t1", id);
    }

    // ----- CHANGE TASK -----
    @Test
    void changeTask_shouldUpdateFields() {
        Task task = new Task();
        when(taskRepository.findById("t1"))
                .thenReturn(Optional.of(task));

        Task result = taskService.changeTask("t1", "new", "desc");

        assertEquals("new", result.getTitle());
        assertEquals("desc", result.getDescription());
    }
}
