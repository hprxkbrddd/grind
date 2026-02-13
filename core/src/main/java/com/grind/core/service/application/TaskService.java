package com.grind.core.service.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grind.core.dto.entity.TaskDTO;
import com.grind.core.enums.TaskStatus;
import com.grind.core.exception.InvalidAggregateStateException;
import com.grind.core.exception.SprintNotFoundException;
import com.grind.core.exception.TaskNotFoundException;
import com.grind.core.exception.TrackNotFoundException;
import com.grind.core.model.Sprint;
import com.grind.core.model.Task;
import com.grind.core.model.Track;
import com.grind.core.repository.SprintRepository;
import com.grind.core.repository.TaskRepository;
import com.grind.core.repository.TrackRepository;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Validated
public class TaskService {

    private final TaskRepository taskRepository;
    private final TrackRepository trackRepository;
    private final SprintRepository sprintRepository;
    private final ObjectMapper objectMapper;

    @PreAuthorize("hasRole('ADMIN')")
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task getById(
            @NotBlank(message = "Task id must be not null or blank")
            String id
    ) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    public List<Task> getBySprint(
            @NotBlank(message = "Sprint id must be not null or blank")
            String sprintId
    ) {
        if (!sprintRepository.existsById(sprintId))
            throw new SprintNotFoundException(sprintId);
        List<Task> res = taskRepository.findBySprintId(sprintId);
        if (res.isEmpty())
            throw new InvalidAggregateStateException(Sprint.class, Task.class);
        return res;
    }

    public List<Task> getByTrack(
            @NotBlank(message = "Track id must be not null or blank")
            String trackId
    ) {
        if (!trackRepository.existsById(trackId))
            throw new TrackNotFoundException(trackId);
        List<Task> res = taskRepository.findByTrackId(trackId);
        if (res.isEmpty())
            throw new InvalidAggregateStateException(Track.class, Task.class);
        return res;
    }

    public List<TaskDTO> getBySprintAndStatus(String sprintId, TaskStatus status) {
        return taskRepository.findBySprintIdAndStatus(sprintId, status)
                .stream().map(Task::mapDTO)
                .toList();
    }

    @Transactional
    public Task createTask(
            @NotBlank(message = "Task title must not be null or blank")
            String title,
            @NotBlank(message = "Task trackId must not be null or blank")
            String trackId,
            @NotNull(message = "Track id must be not null or blank")
            String description
    ) {
        Task task = new Task();
        task.setTrack(trackRepository.findById(trackId)
                .orElseThrow(() -> new TrackNotFoundException(trackId, "Task creation failed")));
        task.setTitle(title);
        task.setDescription(description);
        task.setStatus(TaskStatus.CREATED);
        taskRepository.save(task);
        return task;
    }

    @Transactional
    public Task planTaskSprint(
            @NotBlank(message = "Task id must not be null or blank")
            String taskId,
            @NotBlank(message = "Sprint id must not be null or blank")
            String sprintId,
            @NotNull(message = "Day of sprint must not be null")
            @PositiveOrZero(message = "Day of sprint must be non-negative")
            Integer dayOfSprint
    ) {
        Sprint sprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new SprintNotFoundException(sprintId, "Could not plan task for sprint"));
        int sprintLen = Math.toIntExact(ChronoUnit.DAYS.between(sprint.getStartDate(), sprint.getEndDate()) + 1);
        if (dayOfSprint >= sprintLen || dayOfSprint < 0)
            throw new IllegalArgumentException("Day must be less than sprint's length");
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId, "Could not plan task for sprint"));
        if (!task.getTrack().getId().equals(sprint.getTrack().getId()))
            throw new IllegalArgumentException("Could not plan task for sprint.\nTask:" + taskId + " and sprint:" + sprintId + " do not belong to one track");

        LocalDate plannedDate = sprint.getStartDate().plusDays(dayOfSprint);
        task.setPlannedDate(plannedDate);
        task.setSprint(sprint);
        task.setStatus(TaskStatus.PLANNED);
        return task;
    }

    @Transactional
    public Task planTaskByDate(
            @NotBlank(message = "Task id must not be null or blank")
            String taskId,
            @NotNull(message = "Planned date must not be null")
            LocalDate date) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId, "Could not plan task for date"));
        Sprint sprint = sprintRepository
                .findByTrack_IdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        task.getTrack().getId(),
                        date,
                        date
                )
                .orElseThrow(() -> new IllegalArgumentException("Provided date has to be within the track's time limits"));
        task.setPlannedDate(date);
        task.setSprint(sprint);
        task.setStatus(TaskStatus.PLANNED);
        return task;
    }

    @Transactional
    public Task completeTask(
            @NotBlank(message = "Task id must not be null or blank")
            String taskId
    ) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task has not been marked as completed"));
        task.setActualDate(LocalDate.now());
        task.setStatus(TaskStatus.COMPLETED);
        return task;
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public List<Task> markOverdue() {
        List<Task> toMark = taskRepository.getOverdueWithStatus(LocalDate.now(), TaskStatus.PLANNED);
        toMark.forEach(t -> t.setStatus(TaskStatus.OVERDUE));
        return toMark;
    }

    @Transactional
    public String deleteTask(
            @NotBlank(message = "Task id must not be null or blank")
            String taskId
    ) {
        taskRepository.deleteById(taskId);
        return taskId;
    }

    @Transactional
    public Task changeTask(
            @NotBlank(message = "Task id must not be null or blank")
            String taskId,
            @NotBlank(message = "Task title must not be null or blank")
            String title,
            @NotNull(message = "Task description must not be null")
            String description) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId, "Task has not been changed"));
        if (description != null) task.setDescription(description);
        if (title != null) task.setTitle(title);
        return task;
    }
}
