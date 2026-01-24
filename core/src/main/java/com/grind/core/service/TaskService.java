package com.grind.core.service;

import com.grind.core.dto.TaskDTO;
import com.grind.core.dto.TaskStatus;
import com.grind.core.model.Sprint;
import com.grind.core.model.Task;
import com.grind.core.repository.SprintRepository;
import com.grind.core.repository.TaskRepository;
import com.grind.core.repository.TrackRepository;
import com.grind.core.request.Task.ChangeTaskRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TrackRepository trackRepository;
    private final SprintRepository sprintRepository;

    @PreAuthorize("hasRole('ADMIN')")
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public TaskDTO getById(String id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tried to fetch by id, but not found"))
                .mapDTO();
    }

    public List<Task> getBySprint(String sprintId) {
        return taskRepository.findBySprintId(sprintId);
    }

    public List<Task> getByTrack(String trackId) {
        return taskRepository.findByTrackId(trackId);
    }

    public List<TaskDTO> getBySprintAndStatus(String sprintId, TaskStatus status) {
        return taskRepository.findBySprintIdAndStatus(sprintId, status)
                .stream().map(Task::mapDTO)
                .toList();
    }

    @Transactional
    public Task createTask(
            String title,
            String trackId,
            String description
    ) {
        Task task = new Task();
        task.setTrack(trackRepository.findById(trackId)
                .orElseThrow(() -> new EntityNotFoundException("non-existing track has been provided")));
        task.setTitle(title);
        task.setDescription(description);
        task.setStatus(TaskStatus.CREATED);
        taskRepository.save(task);
        return task;
    }

    @Transactional
    public Task planTaskSprint(String taskId, String sprintId, Integer dayOfSprint) {
        Sprint sprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new EntityNotFoundException("non-existing sprint has been provided"));
        Integer sprintLen = Math.toIntExact(ChronoUnit.DAYS.between(sprint.getStartDate(), sprint.getEndDate()) + 1);
        if (dayOfSprint >= sprintLen || dayOfSprint < 0) {
            throw new IllegalArgumentException("day of sprint must be non-negative and less than sprint_length");
        }
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("could not plan task. task is not in db"));
        if (!task.getTrack().getId().equals(sprint.getTrack().getId()))
            throw new IllegalArgumentException("task does not match sprint");

        LocalDate plannedDate = sprint.getStartDate().plusDays(dayOfSprint);
        task.setPlannedDate(plannedDate);
        task.setSprint(sprint);
        task.setStatus(TaskStatus.PLANNED);
        return task;
    }

    @Transactional
    public Task planTaskByDate(String taskId, LocalDate date) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("could not plan task. task is not in db"));

        Sprint sprint = sprintRepository
                .findByTrack_IdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        task.getTrack().getId(),
                        date,
                        date
                )
                .orElseThrow(() -> new EntityNotFoundException("no sprint found for given date"));

        task.setPlannedDate(date);
        task.setSprint(sprint);
        task.setStatus(TaskStatus.PLANNED);

        return task;
    }

    @Transactional
    public Task completeTask(String taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("could not plan task. task is not in db"));
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
    public String deleteTask(String id) {
        taskRepository.deleteById(id);
        return id;
    }

    @Transactional
    public Task changeTask(String taskId, String title, String description) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Could not find task with id:" + taskId));
        if (description != null) task.setDescription(description);
        if (title != null) task.setTitle(title);
        return task;
    }
}
