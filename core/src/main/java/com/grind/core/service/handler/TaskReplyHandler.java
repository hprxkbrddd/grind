package com.grind.core.service.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grind.core.dto.request.task.ChangeTaskRequest;
import com.grind.core.dto.request.task.CreateTaskRequest;
import com.grind.core.dto.request.task.PlanTaskDateDTO;
import com.grind.core.dto.request.task.PlanTaskSprintDTO;
import com.grind.core.dto.wrap.Reply;
import com.grind.core.enums.CoreMessageType;
import com.grind.core.model.Task;
import com.grind.core.service.application.TaskService;
import com.grind.core.util.ActionReplyExecutor;
import com.grind.core.util.IdParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskReplyHandler {

    private final TaskService service;
    private final ObjectMapper objectMapper;
    private final ActionReplyExecutor exec;

    public Reply handleGetTasksOfTrack(String payload) {
        return exec.withErrorMapping(() ->
                Reply.ok(
                        CoreMessageType.TASKS_OF_TRACK,
                        service.getByTrack(
                                IdParser.run(payload)
                        ).stream().map(Task::mapDTO).toList()
                )
        );

    }

    public Reply handleGetTasksOfSprint(String payload) {
        return exec.withErrorMapping(() ->
                Reply.ok(
                        CoreMessageType.TASKS_OF_SPRINT,
                        service.getBySprint(
                                IdParser.run(payload)
                        ).stream().map(Task::mapDTO).toList()
                )
        );
    }

    public Reply handleGetTask(String payload) {
        return exec.withErrorMapping(() ->
                Reply.ok(
                        CoreMessageType.TASK,
                        service.getById(
                                IdParser.run(payload)
                        ).mapDTO()
                )
        );

    }

    public Reply handleGetAllTasks() {
        return exec.withErrorMapping(() ->
                Reply.ok(
                        CoreMessageType.ALL_TASKS,
                        service.getAllTasks()
                                .stream().map(Task::mapDTO).toList()
                )
        );
    }

    public Reply handleChangeTask(String payload) {
        return exec.withErrorMapping(() -> {
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
        return exec.withErrorMapping(() -> {
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
        return exec.withErrorMapping(() -> {
            PlanTaskDateDTO req = objectMapper.readValue(payload, PlanTaskDateDTO.class);
            return Reply.ok(
                    CoreMessageType.TASK_PLANNED,
                    service.planTaskByDate(req.taskId(), req.plannedDate())
                            .mapDTO()
            );
        });
    }

    public Reply handleCompleteTask(String payload) {
        return exec.withErrorMapping(() ->
                Reply.ok(
                        CoreMessageType.TASK_COMPLETED,
                        service.completeTask(
                                IdParser.run(payload)
                        ).mapDTO()
                )
        );
    }

    public Reply handleTaskToBacklog(String payload) {
        return exec.withErrorMapping(() ->
                Reply.ok(
                        CoreMessageType.TASK_AT_BACKLOG,
                        service.toBackLog(
                                IdParser.run(payload)
                        ).mapDTO()
                )
        );
    }

    public Reply handleCreateTask(String payload) {
        return exec.withErrorMapping(() -> {
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

    public Reply handleDeleteTask(String payload) {
        return exec.withErrorMapping(() ->
                Reply.ok(
                        CoreMessageType.TASK_DELETED,
                        service.deleteTask(
                                IdParser.run(payload)
                        )
                )
        );
    }
}
