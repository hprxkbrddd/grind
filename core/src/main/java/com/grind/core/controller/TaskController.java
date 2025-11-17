package com.grind.core.controller;

import com.grind.core.model.Task;
import com.grind.core.dto.TaskDTO;
import com.grind.core.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/Task")
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/create")
    public ResponseEntity<Task> createTask(@RequestBody TaskDTO taskDTO){
        Task task = taskService.createTask(taskDTO);

        return new ResponseEntity<>(task, HttpStatus.CREATED);
    }

    //public ResponseEntity<Task> setCompleted(@PathVariable Long id){}

    //public ResponseEntity<Task> setExpired(@PathVariable Long id){}

    //public ResponseEntity<Task> deleteTask(@PathVariable Long id){}
}
