package com.grind.core.controller;

import com.grind.core.model.Task;
import com.grind.core.dto.TaskDTO;
import com.grind.core.request.Task.ChangeTaskDescriptionRequest;
import com.grind.core.request.Task.ChangeTaskPlannedDate;
import com.grind.core.request.Task.ChangeTaskTitleRequest;
import com.grind.core.request.Task.CreateTaskRequest;
import com.grind.core.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("core/v1/task")
public class TaskController {

    private final TaskService taskService;

    @GetMapping("/get-all")
    public ResponseEntity<List<TaskDTO>> taskIndex(){
        List<Task> tasks = taskService.getAllTasks();
        List<TaskDTO> taskDTOS = tasks.stream().map(Task::mapDTO).collect(Collectors.toList());

        return new ResponseEntity<>(taskDTOS,
                taskDTOS.isEmpty()?
                        HttpStatus.NO_CONTENT : HttpStatus.FOUND);
    }

    @GetMapping("/get-task/{id}")
    public ResponseEntity<TaskDTO> getById(@PathVariable String id){
        return new ResponseEntity<>(taskService.getById(id).mapDTO(), HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<TaskDTO> createTask(@RequestBody CreateTaskRequest createTaskRequest){
        Task task = taskService.createTask(createTaskRequest);

        return new ResponseEntity<>(task.mapDTO(), HttpStatus.CREATED);
    }

    @PutMapping("/set-completed-task/{id}") //setCompleted должен задавать actualDate
    public void setCompleted(@PathVariable String id){
        taskService.completeTask(id);
    }

    @PutMapping("/set-expired-task/{id}")
    public void setExpired(@PathVariable String id){
        taskService.expireTask(id);
    }

    @PutMapping("/change-title")
    public void changeTitle(@RequestBody ChangeTaskTitleRequest changeTaskTitleRequest){
        taskService.changeTitle(changeTaskTitleRequest);
    }

    @PutMapping("/change-planned-date")
    public void changePlannedDate(@RequestBody ChangeTaskPlannedDate changeTaskPlannedDate){
        taskService.changePlannedDate(changeTaskPlannedDate);
    }

    @PutMapping("/change-description")
    public void changeDescription(@RequestBody ChangeTaskDescriptionRequest changeTaskDescriptionRequest){
        taskService.changeDescription(changeTaskDescriptionRequest);
    }

    @DeleteMapping("/delete-task/{id}")
    public void deleteTask(@PathVariable String id){
        taskService.deleteTask(id);
    }
}
