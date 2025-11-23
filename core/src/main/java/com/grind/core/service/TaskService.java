package com.grind.core.service;

import com.grind.core.model.Task;
import com.grind.core.repository.TaskRepository;
import com.grind.core.request.Task.ChangeTaskDescriptionRequest;
import com.grind.core.request.Task.ChangeTaskNameRequest;
import com.grind.core.request.Task.CreateTaskRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    public List<Task> getAllTasks(){ return taskRepository.getAllTasks(); }

    public Task getById(String id){ return taskRepository.getByID(id); }

    public Task createTask(CreateTaskRequest createTaskRequest){
        Task task = new Task();
        task.setName(createTaskRequest.getName());
        task.setDescription(createTaskRequest.getDescription());
        taskRepository.save(task);
        return task;
    }

    public void completeTask(String id){ taskRepository.completeTask(id); }

    public void expireTask(String id){ taskRepository.expireTask(id); }

    public void changeName(ChangeTaskNameRequest changeTaskNameRequest){ taskRepository.changeName(changeTaskNameRequest); }

    public void changeDescription(ChangeTaskDescriptionRequest changeTaskDescriptionRequest){ taskRepository.changeDescription(changeTaskDescriptionRequest);}

    public void deleteTask(String id){ taskRepository.deleteTask(id); }
}
