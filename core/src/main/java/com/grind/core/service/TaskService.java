package com.grind.core.service;

import com.grind.core.model.Task;
import com.grind.core.repository.SprintRepository;
import com.grind.core.repository.TaskRepository;
import com.grind.core.request.Task.ChangeTaskRequest;
import com.grind.core.request.Task.CreateTaskRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    private final SprintRepository sprintRepository;

    public List<Task> getAllTasks(){ return taskRepository.getAllTasks(); }

    public Task getById(String id){ return taskRepository.getByID(id); }

    public Task createTask(CreateTaskRequest createTaskRequest){
        Task task = new Task();
        task.setTitle(createTaskRequest.title());
        task.setSprint(sprintRepository.getById(createTaskRequest.sprint_id()));
        task.setPlannedDate(createTaskRequest.plannedDate());
        task.setDescription(createTaskRequest.description());
        task.setStatus(createTaskRequest.status());
        taskRepository.save(task);
        return task;
    }

    public void completeTask(String id){ taskRepository.completeTask(id); }

    public void expireTask(String id){ taskRepository.expireTask(id); }

    public void deleteTask(String id){ taskRepository.deleteTask(id); }

    public void changeTask(ChangeTaskRequest changeTaskRequest) {}
}
