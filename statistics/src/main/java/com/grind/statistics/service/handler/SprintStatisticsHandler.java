package com.grind.statistics.service.handler;

import com.grind.statistics.dto.response.sprint.*;
import com.grind.statistics.dto.wrap.Reply;
import com.grind.statistics.enums.StatisticsMessageType;
import com.grind.statistics.service.application.SprintService;
import com.grind.statistics.util.ActionReplyExecutor;
import com.grind.statistics.util.IdParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SprintStatisticsHandler {
    private final SprintService sprintService;
    private final ActionReplyExecutor exec;

    public Reply<?> routeReply(StatisticsMessageType type, String payload) {
        switch (type) {
            case GET_SPRINT_COMPLETION -> {
                return handleGetSprintCompletion(payload);
            }
            case GET_SPRINT_REMAINING_LOAD -> {
                return handleGetRemainingLoad(payload);
            }
            case GET_SPRINT_OVERDUE_PRESSURE -> {
                return handleGetOverduePressure(payload);
            }
            case GET_SPRINT_ACTIVE_TASKS_AGING -> {
                return handleActiveTasksAging(payload);
            }
            case GET_SPRINT_WORK_IN_PROGRESS -> {
                return handleGetWorkInProgress(payload);
            }
            case GET_SPRINT_OVERDUE_AMONG_COMPLETED -> {
                return handleOverdueAmongCompleted(payload);
            }
            default -> throw new UnsupportedOperationException("Message type is not related to sprint statistics");
        }
    }

    private Reply<SprintCompletionDTO> handleGetSprintCompletion(String payload) {
        return exec.withErrorMapping(() ->
                Reply.ok(
                        StatisticsMessageType.SPRINT_COMPLETION,
                        sprintService.getSprintCompletion(
                                IdParser.run(payload)
                        )
                )
        );
    }

    private Reply<SprintOverdueAmongCompletedDTO> handleOverdueAmongCompleted(String payload) {
        return exec.withErrorMapping(() ->
                Reply.ok(
                        StatisticsMessageType.SPRINT_OVERDUE_AMONG_COMPLETED,
                        sprintService.getOverdueAmongCompleted(
                                IdParser.run(payload)
                        )
                )
        );
    }

    private Reply<SprintWIPDTO> handleGetWorkInProgress(String payload) {
        return exec.withErrorMapping(() ->
                Reply.ok(
                        StatisticsMessageType.SPRINT_WORK_IN_PROGRESS,
                        sprintService.getWIP(
                                IdParser.run(payload)
                        )
                )
        );
    }

    private Reply<SprintActiveTasksAgingDTO> handleActiveTasksAging(String payload) {
        return exec.withErrorMapping(() ->
                Reply.ok(
                        StatisticsMessageType.SPRINT_ACTIVE_TASKS_AGING,
                        sprintService.getActiveTasksAging(
                                IdParser.run(payload)
                        )
                )
        );
    }

    private Reply<SprintOverduePressureDTO> handleGetOverduePressure(String payload) {
        return exec.withErrorMapping(() ->
                Reply.ok(
                        StatisticsMessageType.SPRINT_OVERDUE_PRESSURE,
                        sprintService.getOverduePressure(
                                IdParser.run(payload)
                        )
                )
        );
    }

    private Reply<SprintRemainingLoadDTO> handleGetRemainingLoad(String payload) {
        return exec.withErrorMapping(() ->
                Reply.ok(
                        StatisticsMessageType.SPRINT_REMAINING_LOAD,
                        sprintService.getRemainingLoad(
                                IdParser.run(payload)
                        )
                )
        );
    }
}
