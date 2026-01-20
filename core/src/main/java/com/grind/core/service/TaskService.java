package com.grind.core.service;

import com.grind.core.dto.TaskDTO;
import com.grind.core.model.Task;
import com.grind.core.repository.SprintRepository;
import com.grind.core.repository.TaskRepository;
import com.grind.core.request.Task.ChangeTaskRequest;
import com.grind.core.request.Task.CreateTaskRequest;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final SprintRepository sprintRepository;

    @PreAuthorize("hasRole('ADMIN'")
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public TaskDTO getById(String id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tried to fetch by id, but not found"))
                .mapDTO();
    }

    public List<TaskDTO> getBySprint(String sprintId){
        return taskRepository.findBySprintId(sprintId)
                .stream().map(Task::mapDTO)
                .toList();
    }

    public List<TaskDTO> getBySprintAndStatus(String sprintId, String status){
        return taskRepository.findBySprintIdAndStatus(sprintId, status)
                .stream().map(Task::mapDTO)
                .toList();
    }

    @Transactional
    public Task createTask(CreateTaskRequest createTaskRequest) {
        Task task = new Task();
        task.setTitle(createTaskRequest.title());
        task.setSprint(sprintRepository.getById(createTaskRequest.sprint_id()));
        task.setPlannedDate(createTaskRequest.plannedDate());
        task.setDescription(createTaskRequest.description());
        task.setStatus(createTaskRequest.status());
        taskRepository.save(task);
        return task;
    }

    public void completeTask(String id) {
        taskRepository.completeTask(id);
    }

    public void expireTask(String id) {
        taskRepository.expireTask(id);
    }

    public void deleteTask(String id) {
        taskRepository.deleteById(id);
    }

    @Transactional
    public void changeTask(ChangeTaskRequest req) {
        Task task = taskRepository.findById(req.taskId())
                .orElseThrow(() -> new EntityNotFoundException("Could not change task with id:"+req.taskId()));
        if (req.description()!=null) task.setDescription(req.description());
        if (req.title()!=null) task.setTitle(req.title());
        if (req.plannedDate()!=null) task.setPlannedDate(req.plannedDate());
        taskRepository.save(task);
    }
}
