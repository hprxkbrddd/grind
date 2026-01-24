package com.grind.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grind.core.dto.*;
import com.grind.core.exception.InvalidAggregateStateException;
import com.grind.core.model.Task;
import com.grind.core.request.Task.ChangeTaskRequest;
import com.grind.core.request.Task.CreateTaskRequest;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.util.function.ThrowingSupplier;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskReplyHandler {

    private final TaskService service;
    private final ObjectMapper objectMapper;

    private Reply handleWithErrorMapping(ThrowingSupplier<Reply> action) {
        try {
            return action.getWithException();
        } catch (EntityNotFoundException ex) {
            return Reply.error(ex, HttpStatus.NOT_FOUND);
        } catch (InvalidAggregateStateException | IllegalArgumentException | JsonProcessingException |
                 ConstraintViolationException ex) {
            return Reply.error(ex, HttpStatus.BAD_REQUEST);
        } catch (AccessDeniedException ex) {
            return Reply.error(ex, HttpStatus.FORBIDDEN);
        } catch (Exception ex) {
            return Reply.error(ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Reply handleGetTasksOfTrack(String trackId) {
        return handleWithErrorMapping(() ->
                Reply.ok(
                        CoreMessageType.TASKS_OF_TRACK,
                        service.getByTrack(trackId)
                                .stream().map(Task::mapDTO).toList()
                ));
    }

    public Reply handleGetTasksOfSprint(String sprintId) {
        return handleWithErrorMapping(() -> {
            List<TaskDTO> res = service.getBySprint(sprintId)
                    .stream().map(Task::mapDTO).toList();
            return Reply.ok(CoreMessageType.TASKS_OF_SPRINT, res);
        });
    }

    public Reply handleGetTask(String taskId) {
        return handleWithErrorMapping(() ->
                Reply.ok(CoreMessageType.TASK, service.getById(taskId).mapDTO())
        );
    }

    public Reply handleGetAllTasks() {
        return handleWithErrorMapping(() ->
                Reply.ok(CoreMessageType.ALL_TASKS, service.getAllTasks().stream().map(Task::mapDTO).toList())
        );
    }

    public Reply handleChangeTask(String payload) {
        return handleWithErrorMapping(() -> {
            ChangeTaskRequest req = objectMapper.readValue(payload, ChangeTaskRequest.class);
            return Reply.ok(
                    CoreMessageType.TASK_CHANGED,
                    service.changeTask(
                            req.taskId(),
                            req.description(),
                            req.title()
                    ).mapDTO()
            );
        });
    }

    public Reply handlePlanTaskSprint(String payload) {
        return handleWithErrorMapping(() -> {
            PlanTaskSprintDTO req = objectMapper.readValue(payload, PlanTaskSprintDTO.class);
            return Reply.ok(
                    CoreMessageType.TASK_PLANNED,
                    service.planTaskSprint(
                            req.taskId(),
                            req.sprintId(),
                            req.dayOfSprint()
                    ).mapDTO());
        });
    }

    public Reply handlePlanTaskDate(String payload) {
        return handleWithErrorMapping(() -> {
            PlanTaskDateDTO req = objectMapper.readValue(payload, PlanTaskDateDTO.class);
            return Reply.ok(
                    CoreMessageType.TASK_PLANNED,
                    service.planTaskByDate(req.taskId(), req.plannedDate())
                            .mapDTO()
            );
        });
    }

    public Reply handleCompleteTask(String taskId) {
        return handleWithErrorMapping(() ->
                Reply.ok(CoreMessageType.TASK_COMPLETED, service.completeTask(taskId)
                        .mapDTO()));
    }

    public Reply handleCreateTask(String payload) {
        return handleWithErrorMapping(() -> {
            CreateTaskRequest req = objectMapper.readValue(payload, CreateTaskRequest.class);
            return Reply.ok(
                    CoreMessageType.TASK_CREATED,
                    service.createTask(
                            req.title(),
                            req.trackId(),
                            req.description()
                    ).mapDTO());
        });
    }

    public Reply handleDeleteTask(String taskId) {
        return handleWithErrorMapping(() ->
                Reply.ok(
                        CoreMessageType.TASK_DELETED,
                        service.deleteTask(taskId)
                )
        );
    }
}
