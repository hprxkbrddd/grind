package com.progressapp.progress_tracker.controller;

import com.progressapp.progress_tracker.entity.Sprint;
import com.progressapp.progress_tracker.entity.Task;
import com.progressapp.progress_tracker.repository.SprintRepository;
import com.progressapp.progress_tracker.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private SprintRepository sprintRepository;

    // GET /api/tasks - получить все задачи
    @GetMapping
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    // GET /api/tasks/{id} - получить задачу по ID
    @GetMapping("/{id}")
    public Task getTaskById(@PathVariable UUID id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
    }

    // POST /api/tasks - создать новую задачу
    @PostMapping
    public Task createTask(@RequestBody Task task) {
        // Проверяем что спринт существует
        Sprint sprint = sprintRepository.findById(task.getSprint().getId())
                .orElseThrow(() -> new RuntimeException("Sprint not found with id: " + task.getSprint().getId()));
        task.setSprint(sprint);

        return taskRepository.save(task);
    }

    // PUT /api/tasks/{id} - обновить задачу
    @PutMapping("/{id}")
    public Task updateTask(@PathVariable UUID id, @RequestBody Task taskDetails) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));

        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setPlannedDate(taskDetails.getPlannedDate());
        task.setActualDate(taskDetails.getActualDate());
        task.setStatus(taskDetails.getStatus());
        task.setPriority(taskDetails.getPriority());
        task.setOrderIndex(taskDetails.getOrderIndex());

        return taskRepository.save(task);
    }

    // DELETE /api/tasks/{id} - удалить задачу
    @DeleteMapping("/{id}")
    public String deleteTask(@PathVariable UUID id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));

        taskRepository.delete(task);
        return "Task deleted successfully";
    }

    // GET /api/tasks/sprint/{sprintId} - получить все задачи спринта
    @GetMapping("/sprint/{sprintId}")
    public List<Task> getTasksBySprint(@PathVariable UUID sprintId) {
        Sprint sprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new RuntimeException("Sprint not found with id: " + sprintId));
        return taskRepository.findBySprint(sprint);
    }

    // GET /api/tasks/status/{status} - получить задачи по статусу
    @GetMapping("/status/{status}")
    public List<Task> getTasksByStatus(@PathVariable String status) {
        return taskRepository.findByStatus(status);
    }
}