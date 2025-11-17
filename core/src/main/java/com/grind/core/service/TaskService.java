package com.grind.core.service;

import com.grind.core.model.Task;
import com.grind.core.dto.TaskDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskService {

    public Task createTask(TaskDTO taskDTO){
        Task task = new Task();
        task.setName(taskDTO.getName());
        task.setDescription(taskDTO.getDescription());
        return task;
    }

    //public Task completeTask(Long taskID){}
    //public Task expireTask(Long taskID){}
    //public Task deleteTask(Long taskID){}
}
