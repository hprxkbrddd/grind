package com.grind.gateway.controller;

import com.grind.gateway.dto.Body;
import com.grind.gateway.dto.core.task.ChangeTaskDTO;
import com.grind.gateway.dto.core.task.CreateTaskRequest;
import com.grind.gateway.dto.core.task.PlanTaskDateDTO;
import com.grind.gateway.dto.core.task.PlanTaskSprintDTO;
import com.grind.gateway.dto.core.task.TaskDTO;
import com.grind.gateway.service.core.CoreTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/core/task")
@RequiredArgsConstructor
@ApiResponses({
    @ApiResponse(responseCode = "400", description = "Invalid request",
            content = @Content(schema = @Schema(implementation = String.class),
                    examples = {
                            @ExampleObject(name = "blankId", value = "Task id must be not null or blank"),
                            @ExampleObject(name = "invalidDay", value = "Day must be less than sprint's length"),
                            @ExampleObject(name = "outOfRange", value = "Provided date has to be within the track's time limits"),
                            @ExampleObject(name = "trackMismatch", value = "Could not plan task for sprint.\nTask:task-123 and sprint:sprint-1 do not belong to one track"),
                            @ExampleObject(name = "notModified", value = "Task was not modified")
                    })),
    @ApiResponse(responseCode = "403", description = "Access denied",
            content = @Content(schema = @Schema(implementation = String.class),
                    examples = @ExampleObject(name = "forbidden", value = "Access denied"))),
    @ApiResponse(responseCode = "404", description = "Resource not found",
            content = @Content(schema = @Schema(implementation = String.class),
                    examples = {
                            @ExampleObject(name = "taskNotFound", value = "There is not task with id:task-123"),
                            @ExampleObject(name = "sprintNotFound", value = "There is not sprint with id:sprint-1"),
                            @ExampleObject(name = "trackNotFound", value = "There is not track with id:track-1")
                    })),
    @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(schema = @Schema(implementation = String.class),
                    examples = @ExampleObject(
                            name = "internalError",
                            value = "Internal server error"
                    )))
})
public class CoreTaskController {

    private final CoreTaskService taskService;

    @GetMapping("/all")
    @Operation(summary = "Get all tasks")
    @ApiResponse(responseCode = "200", description = "Task list",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = TaskDTO.class)),
                    examples = @ExampleObject(
                            name = "taskList",
                            value = "[{\"id\":\"task-123\",\"title\":\"Write docs\",\"sprint_id\":\"sprint-1\",\"track_id\":\"track-1\",\"plannedDate\":\"2024-05-10\",\"actualDate\":\"2024-05-11\",\"description\":\"Describe work\",\"status\":\"PLANNED\",\"createdAt\":\"2024-05-01T10:15:30\",\"version\":1},{\"id\":\"task-124\",\"title\":\"Review PR\",\"sprint_id\":\"sprint-1\",\"track_id\":\"track-1\",\"plannedDate\":\"2024-05-12\",\"actualDate\":null,\"description\":\"Review changes\",\"status\":\"PLANNED\",\"createdAt\":\"2024-05-02T09:00:00\",\"version\":1}]"
                    )))
    public ResponseEntity<Object> getAllTasks() {
        Body<?> body = taskService.callGetAllTasks();
        return ResponseEntity.status(body.status())
                .body(body.error() == null ?
                        body.payload() : body.error()
                );
    }

    @GetMapping("/{taskId}")
    @Operation(summary = "Get task by id")
    @ApiResponse(responseCode = "200", description = "Task details",
            content = @Content(schema = @Schema(implementation = TaskDTO.class),
                    examples = @ExampleObject(
                            name = "task",
                            value = "{\"id\":\"task-123\",\"title\":\"Write docs\",\"sprint_id\":\"sprint-1\",\"track_id\":\"track-1\",\"plannedDate\":\"2024-05-10\",\"actualDate\":\"2024-05-11\",\"description\":\"Describe work\",\"status\":\"PLANNED\",\"createdAt\":\"2024-05-01T10:15:30\",\"version\":1}"
                    )))
    public ResponseEntity<Object> getTask(@PathVariable String taskId) {
        Body<?> body = taskService.callGetTask(taskId);
        return ResponseEntity.status(body.status())
                .body(body.error() == null ?
                        body.payload() : body.error()
                );
    }

    @GetMapping("/sprint/{sprintId}")
    @Operation(summary = "Get tasks by sprint")
    @ApiResponse(responseCode = "200", description = "Sprint tasks",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = TaskDTO.class)),
                    examples = @ExampleObject(
                            name = "sprintTasks",
                            value = "[{\"id\":\"task-123\",\"title\":\"Write docs\",\"sprint_id\":\"sprint-1\",\"track_id\":\"track-1\",\"plannedDate\":\"2024-05-10\",\"actualDate\":\"2024-05-11\",\"description\":\"Describe work\",\"status\":\"PLANNED\",\"createdAt\":\"2024-05-01T10:15:30\",\"version\":1}]"
                    )))
    public ResponseEntity<Object> getTasksOfSprint(@PathVariable String sprintId) {
        Body<?> body = taskService.callGetTasksOfSprint(sprintId);
        return ResponseEntity.status(body.status())
                .body(body.error() == null ?
                        body.payload() : body.error()
                );
    }

    @GetMapping("/track/{trackId}")
    @Operation(summary = "Get tasks by track")
    @ApiResponse(responseCode = "200", description = "Track tasks",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = TaskDTO.class)),
                    examples = @ExampleObject(
                            name = "trackTasks",
                            value = "[{\"id\":\"task-123\",\"title\":\"Write docs\",\"sprint_id\":\"sprint-1\",\"track_id\":\"track-1\",\"plannedDate\":\"2024-05-10\",\"actualDate\":\"2024-05-11\",\"description\":\"Describe work\",\"status\":\"PLANNED\",\"createdAt\":\"2024-05-01T10:15:30\",\"version\":1}]"
                    )))
    public ResponseEntity<Object> getTasksOfTrack(@PathVariable String trackId) {
        Body<?> body = taskService.callGetTasksOfTrack(trackId);
        return ResponseEntity.status(body.status())
                .body(body.error() == null ?
                        body.payload() : body.error()
                );
    }

    @PostMapping
    @Operation(summary = "Create a task")
    @ApiResponse(responseCode = "200", description = "Task created",
            content = @Content(schema = @Schema(implementation = TaskDTO.class),
                    examples = @ExampleObject(
                            name = "created",
                            value = "{\"id\":\"task-123\",\"title\":\"Write docs\",\"sprint_id\":\"00000000-0000-0000-0000-000000000000\",\"track_id\":\"track-1\",\"plannedDate\":null,\"actualDate\":null,\"description\":\"Describe work\",\"status\":\"CREATED\",\"createdAt\":\"2024-05-01T10:15:30\",\"version\":1}"
                    )))
    public ResponseEntity<?> create(@RequestBody CreateTaskRequest dto) {
        Body<?> body = taskService.callCreateTask(dto);
        return ResponseEntity.status(body.status())
                .body(body.error() == null ?
                        body.payload() : body.error()
                );
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update task details")
    @ApiResponse(responseCode = "200", description = "Task updated",
            content = @Content(schema = @Schema(implementation = TaskDTO.class),
                    examples = @ExampleObject(
                            name = "updated",
                            value = "{\"id\":\"task-123\",\"title\":\"Write docs\",\"sprint_id\":\"sprint-1\",\"track_id\":\"track-1\",\"plannedDate\":\"2024-05-10\",\"actualDate\":null,\"description\":\"Describe work\",\"status\":\"PLANNED\",\"createdAt\":\"2024-05-01T10:15:30\",\"version\":2}"
                    )))
    public ResponseEntity<?> changeTask(@RequestBody ChangeTaskDTO dto, @PathVariable String id) {
        dto.setTaskId(id);
        Body<?> body = taskService.callChangeTask(dto);
        return ResponseEntity.status(body.status())
                .body(body.error() == null ?
                        body.payload() : body.error()
                );
    }

    @PutMapping("/{taskId}/plan/sprint")
    @Operation(summary = "Plan task in sprint")
    @ApiResponse(responseCode = "200", description = "Task planned in sprint",
            content = @Content(schema = @Schema(implementation = TaskDTO.class),
                    examples = @ExampleObject(
                            name = "plannedSprint",
                            value = "{\"id\":\"task-123\",\"title\":\"Write docs\",\"sprint_id\":\"sprint-1\",\"track_id\":\"track-1\",\"plannedDate\":\"2024-05-10\",\"actualDate\":null,\"description\":\"Describe work\",\"status\":\"PLANNED\",\"createdAt\":\"2024-05-01T10:15:30\",\"version\":2}"
                    )))
    public ResponseEntity<?> planTaskSprint(@RequestBody PlanTaskSprintDTO dto, @PathVariable String taskId) {
        dto.setTaskId(taskId);
        Body<?> body = taskService.callPlanTaskSprint(dto);
        return ResponseEntity.status(body.status())
                .body(body.error() == null ?
                        body.payload() : body.error()
                );
    }

    @PutMapping("/{taskId}/plan/date")
    @Operation(summary = "Plan task by date")
    @ApiResponse(responseCode = "200", description = "Task planned by date",
            content = @Content(schema = @Schema(implementation = TaskDTO.class),
                    examples = @ExampleObject(
                            name = "plannedDate",
                            value = "{\"id\":\"task-123\",\"title\":\"Write docs\",\"sprint_id\":\"sprint-1\",\"track_id\":\"track-1\",\"plannedDate\":\"2024-05-10\",\"actualDate\":null,\"description\":\"Describe work\",\"status\":\"PLANNED\",\"createdAt\":\"2024-05-01T10:15:30\",\"version\":2}"
                    )))
    public ResponseEntity<?> planTaskDate(@RequestBody PlanTaskDateDTO dto, @PathVariable String taskId) {
        dto.setTaskId(taskId);
        Body<?> body = taskService.callPlanTaskDate(dto);
        return ResponseEntity.status(body.status())
                .body(body.error() == null ?
                        body.payload() : body.error()
                );
    }

    @PutMapping("/{taskId}/complete")
    @Operation(summary = "Mark task complete")
    @ApiResponse(responseCode = "200", description = "Task completed",
            content = @Content(schema = @Schema(implementation = TaskDTO.class),
                    examples = @ExampleObject(
                            name = "completed",
                            value = "{\"id\":\"task-123\",\"title\":\"Write docs\",\"sprint_id\":\"sprint-1\",\"track_id\":\"track-1\",\"plannedDate\":\"2024-05-10\",\"actualDate\":\"2024-05-11\",\"description\":\"Describe work\",\"status\":\"COMPLETED\",\"createdAt\":\"2024-05-01T10:15:30\",\"version\":3}"
                    )))
    public ResponseEntity<?> completeTask(@PathVariable String taskId) {
        Body<?> body = taskService.callCompleteTask(taskId);
        return ResponseEntity.status(body.status())
                .body(body.error() == null ?
                        body.payload() : body.error()
                );
    }

    @PutMapping("/{taskId}/backlog")
    @Operation(summary = "Move task to backlog")
    @ApiResponse(responseCode = "200", description = "Task moved to backlog",
            content = @Content(schema = @Schema(implementation = TaskDTO.class),
                    examples = @ExampleObject(
                            name = "backlog",
                            value = "{\"id\":\"task-123\",\"title\":\"Write docs\",\"sprint_id\":\"00000000-0000-0000-0000-000000000000\",\"track_id\":\"track-1\",\"plannedDate\":null,\"actualDate\":null,\"description\":\"Describe work\",\"status\":\"CREATED\",\"createdAt\":\"2024-05-01T10:15:30\",\"version\":3}"
                    )))
    public ResponseEntity<?> moveTaskToBackLog(@PathVariable String taskId) {
        Body<?> body = taskService.callTaskToBacklog(taskId);
        return ResponseEntity.status(body.status())
                .body(body.error() == null ?
                        body.payload() : body.error()
                );
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete task")
    @ApiResponse(responseCode = "200", description = "Task deleted",
            content = @Content(schema = @Schema(implementation = TaskDTO.class),
                    examples = @ExampleObject(
                            name = "deleted",
                            value = "{\"id\":\"task-123\",\"title\":\"Write docs\",\"sprint_id\":\"sprint-1\",\"track_id\":\"track-1\",\"plannedDate\":\"2024-05-10\",\"actualDate\":null,\"description\":\"Describe work\",\"status\":\"PLANNED\",\"createdAt\":\"2024-05-01T10:15:30\",\"version\":3}"
                    )))
    public ResponseEntity<?> delete(@PathVariable String id) {
        Body<?> body = taskService.callDeleteTask(id);
        return ResponseEntity.status(body.status())
                .body(body.error() == null ?
                        body.payload() : body.error()
                );
    }
}
