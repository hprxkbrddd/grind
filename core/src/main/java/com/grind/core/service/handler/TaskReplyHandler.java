package com.grind.core.service.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grind.core.dto.entity.TaskDTO;
import com.grind.core.dto.request.task.ChangeTaskRequest;
import com.grind.core.dto.request.task.CreateTaskRequest;
import com.grind.core.dto.request.task.PlanTaskDateDTO;
import com.grind.core.dto.request.task.PlanTaskSprintDTO;
import com.grind.core.dto.wrap.Reply;
import com.grind.core.enums.coreMessageTypes.CoreTaskReqMsgType;
import com.grind.core.enums.coreMessageTypes.CoreTaskResMsgType;
import com.grind.core.model.Task;
import com.grind.core.service.application.TaskService;
import com.grind.core.util.ActionReplyExecutor;
import com.grind.core.util.IdParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskReplyHandler {

    private final TaskService service;
    private final ObjectMapper objectMapper;
    private final ActionReplyExecutor exec;

    public Reply<?> routeReply(CoreTaskReqMsgType type, String payload) {
        switch (type) {
            case GET_TASKS_OF_TRACK -> {
                return handleGetTasksOfTrack(payload);
            }
            case GET_TASKS_OF_SPRINT -> {
                return handleGetTasksOfSprint(payload);
            }
            case GET_TASK -> {
                return handleGetTask(payload);
            }
            case GET_ALL_TASKS -> {
                return handleGetAllTasks();
            }
            case CHANGE_TASK -> {
                return handleChangeTask(payload);
            }
            case PLAN_TASK_SPRINT -> {
                return handlePlanTaskSprint(payload);
            }
            case PLAN_TASK_DATE -> {
                return handlePlanTaskDate(payload);
            }
            case COMPLETE_TASK -> {
                return handleCompleteTask(payload);
            }
            case MOVE_TASK_TO_BACKLOG -> {
                return handleTaskToBacklog(payload);
            }
            case CREATE_TASK -> {
                return handleCreateTask(payload);
            }
            case DELETE_TASK -> {
                return handleDeleteTask(payload);
            }
            default -> throw new UnsupportedOperationException("Message type is not related to tasks");
        }
    }

    private Reply<List<TaskDTO>> handleGetTasksOfTrack(String payload) {
        return exec.withErrorMapping(() ->
                Reply.ok(
                        CoreTaskResMsgType.TASKS_OF_TRACK,
                        service.getByTrack(
                                IdParser.run(payload)
                        ).stream().map(Task::mapDTO).toList()
                )
        );
    }

    private Reply<List<TaskDTO>> handleGetTasksOfSprint(String payload) {
        return exec.withErrorMapping(() ->
                Reply.ok(
                        CoreTaskResMsgType.TASKS_OF_SPRINT,
                        service.getBySprint(
                                IdParser.run(payload)
                        ).stream().map(Task::mapDTO).toList()
                )
        );
    }

    private Reply<TaskDTO> handleGetTask(String payload) {
        return exec.withErrorMapping(() ->
                Reply.ok(
                        CoreTaskResMsgType.TASK,
                        service.getById(
                                IdParser.run(payload)
                        ).mapDTO()
                )
        );

    }

    private Reply<List<TaskDTO>> handleGetAllTasks() {
        return exec.withErrorMapping(() ->
                Reply.ok(
                        CoreTaskResMsgType.ALL_TASKS,
                        service.getAllTasks()
                                .stream().map(Task::mapDTO).toList()
                )
        );
    }

    private Reply<TaskDTO> handleChangeTask(String payload) {
        return exec.withErrorMapping(() -> {
            ChangeTaskRequest req = objectMapper.readValue(payload, ChangeTaskRequest.class);
            return Reply.ok(
                    CoreTaskResMsgType.TASK_CHANGED,
                    service.changeTask(
                            req.taskId(),
                            req.description(),
                            req.title()
                    ).mapDTO()
            );
        });
    }

    private Reply<TaskDTO> handlePlanTaskSprint(String payload) {
        return exec.withErrorMapping(() -> {
            PlanTaskSprintDTO req = objectMapper.readValue(payload, PlanTaskSprintDTO.class);
            return Reply.ok(
                    CoreTaskResMsgType.TASK_PLANNED,
                    service.planTaskSprint(
                            req.taskId(),
                            req.sprintId(),
                            req.dayOfSprint()
                    ).mapDTO());
        });
    }

    private Reply<TaskDTO> handlePlanTaskDate(String payload) {
        return exec.withErrorMapping(() -> {
            PlanTaskDateDTO req = objectMapper.readValue(payload, PlanTaskDateDTO.class);
            return Reply.ok(
                    CoreTaskResMsgType.TASK_PLANNED,
                    service.planTaskByDate(req.taskId(), req.plannedDate())
                            .mapDTO()
            );
        });
    }

    private Reply<TaskDTO> handleCompleteTask(String payload) {
        return exec.withErrorMapping(() ->
                Reply.ok(
                        CoreTaskResMsgType.TASK_COMPLETED,
                        service.completeTask(
                                IdParser.run(payload)
                        ).mapDTO()
                )
        );
    }

    private Reply<TaskDTO> handleTaskToBacklog(String payload) {
        return exec.withErrorMapping(() ->
                Reply.ok(
                        CoreTaskResMsgType.TASK_AT_BACKLOG,
                        service.toBackLog(
                                IdParser.run(payload)
                        ).mapDTO()
                )
        );
    }

    private Reply<TaskDTO> handleCreateTask(String payload) {
        return exec.withErrorMapping(() -> {
            CreateTaskRequest req = objectMapper.readValue(payload, CreateTaskRequest.class);
            return Reply.ok(
                    CoreTaskResMsgType.TASK_CREATED,
                    service.createTask(
                            req.title(),
                            req.trackId(),
                            req.description()
                    ).mapDTO());
        });
    }

    private Reply<TaskDTO> handleDeleteTask(String payload) {
        return exec.withErrorMapping(() ->
                Reply.ok(
                        CoreTaskResMsgType.TASK_DELETED,
                        service.deleteTask(
                                IdParser.run(payload)
                        ).mapDTO()
                )
        );
    }
}
