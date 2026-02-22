package com.grind.gateway.controller;

import com.grind.gateway.dto.Body;
import com.grind.gateway.dto.core.task.ChangeTaskDTO;
import com.grind.gateway.dto.core.task.CreateTaskRequest;
import com.grind.gateway.dto.core.task.PlanTaskDateDTO;
import com.grind.gateway.dto.core.task.PlanTaskSprintDTO;
import com.grind.gateway.service.core.CoreTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/core/task")
@RequiredArgsConstructor
public class CoreTaskController {

    private final CoreTaskService taskService;

    @GetMapping("/all")
    public ResponseEntity<Object> getAllTasks() {
        Body<?> body = taskService.callGetAllTasks();
        return ResponseEntity.status(body.status())
                .body(body.error()==null ?
                        body.payload() : body.error()
                );
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<Object> getTask(@PathVariable String taskId) {
        Body<?> body = taskService.callGetTask(taskId);
        return ResponseEntity.status(body.status())
                .body(body.error()==null ?
                        body.payload() : body.error()
                );
    }

    @GetMapping("/sprint/{sprintId}")
    public ResponseEntity<Object> getTasksOfSprint(@PathVariable String sprintId) {
        Body<?> body = taskService.callGetTasksOfSprint(sprintId);
        return ResponseEntity.status(body.status())
                .body(body.error()==null ?
                        body.payload() : body.error()
                );
    }

    @GetMapping("/track/{trackId}")
    public ResponseEntity<Object> getTasksOfTrack(@PathVariable String trackId) {
        Body<?> body = taskService.callGetTasksOfTrack(trackId);
        return ResponseEntity.status(body.status())
                .body(body.error()==null ?
                        body.payload() : body.error()
                );
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateTaskRequest dto) {
        Body<?> body = taskService.callCreateTask(dto);
        return ResponseEntity.status(body.status())
                .body(body.error()==null ?
                        body.payload() : body.error()
                );
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> changeTask(@RequestBody ChangeTaskDTO dto, @PathVariable String id) {
        dto.setTaskId(id);
        Body<?> body = taskService.callChangeTask(dto);
        return ResponseEntity.status(body.status())
                .body(body.error()==null ?
                        body.payload() : body.error()
                );
    }

    @PutMapping("/{taskId}/plan/sprint")
    public ResponseEntity<?> planTaskSprint(@RequestBody PlanTaskSprintDTO dto, @PathVariable String taskId) {
        dto.setTaskId(taskId);
        Body<?> body = taskService.callPlanTaskSprint(dto);
        return ResponseEntity.status(body.status())
                .body(body.error()==null ?
                        body.payload() : body.error()
                );
    }

    @PutMapping("/{taskId}/plan/date")
    public ResponseEntity<?> planTaskDate(@RequestBody PlanTaskDateDTO dto, @PathVariable String taskId) {
        dto.setTaskId(taskId);
        Body<?> body = taskService.callPlanTaskDate(dto);
        return ResponseEntity.status(body.status())
                .body(body.error()==null ?
                        body.payload() : body.error()
                );
    }

    @PutMapping("/{taskId}/complete")
    public ResponseEntity<?> completeTask(@PathVariable String taskId) {
        Body<?> body = taskService.callCompleteTask(taskId);
        return ResponseEntity.status(body.status())
                .body(body.error()==null ?
                        body.payload() : body.error()
                );
    }

    @PutMapping("/{taskId}/backlog")
    public ResponseEntity<?> moveTaskToBackLog(@PathVariable String taskId) {
        Body<?> body = taskService.callTaskToBacklog(taskId);
        return ResponseEntity.status(body.status())
                .body(body.error()==null ?
                        body.payload() : body.error()
                );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        Body<?> body = taskService.callDeleteTask(id);
        return ResponseEntity.status(body.status())
                .body(body.error()==null ?
                        body.payload() : body.error()
                );
    }
}
