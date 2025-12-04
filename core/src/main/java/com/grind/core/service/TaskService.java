package com.grind.core.service;

import com.grind.core.model.Task;
import com.grind.core.repository.TaskRepository;
import com.grind.core.request.Task.ChangeTaskDescriptionRequest;
import com.grind.core.request.Task.ChangeTaskPlannedDate;
import com.grind.core.request.Task.ChangeTaskTitleRequest;
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
        task.setTitle(createTaskRequest.getTitle());
        task.setSprint(createTaskRequest.getSprint());
        task.setPlannedDate(createTaskRequest.getPlannedDate());
        task.setDescription(createTaskRequest.getDescription());
        task.setStatus(createTaskRequest.getStatus());
        taskRepository.save(task);
        return task;
    }

    public void completeTask(String id){ taskRepository.completeTask(id); }

    public void expireTask(String id){ taskRepository.expireTask(id); }

    public void changeTitle(ChangeTaskTitleRequest changeTaskNameRequest){ }

    public void changeDescription(ChangeTaskDescriptionRequest changeTaskDescriptionRequest){ taskRepository.changeDescription(changeTaskDescriptionRequest);}

    public void deleteTask(String id){ taskRepository.deleteTask(id); }

    public void changePlannedDate(ChangeTaskPlannedDate changeTaskPlannedDate){}
}
