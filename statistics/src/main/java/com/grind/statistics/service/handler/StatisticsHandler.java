package com.grind.statistics.service.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grind.statistics.enums.StatisticsMessageType;
import com.grind.statistics.dto.response.TrackCompletionDTO;
import com.grind.statistics.dto.wrap.Reply;
import com.grind.statistics.service.application.ClickhouseService;
import com.grind.statistics.util.ActionReplyExecutor;
import com.grind.statistics.util.IdParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatisticsHandler {

    private final ClickhouseService clickhouseService;
    private final ObjectMapper objectMapper;
    private final ActionReplyExecutor exec;

    public Reply<?> routeReply(StatisticsMessageType type, String payload) {
        switch (type) {
            case GET_TRACK_COMPLETION -> {
                return handleGetTrackCompletion(payload);
            }
            case GET_REMAINING_LOAD -> {
                return handleGetRemainingLoad(payload);
            }
            case GET_OVERDUE_PRESSURE -> {
                return handleGetOverduePressure(payload);
            }
            case GET_ACTIVE_TASKS_AGING -> {
                return handleActiveTasksAging(payload);
            }
            case GET_WORK_IN_PROGRESS -> {
                return handleGetWorkInProgress(payload);
            }
            case GET_OVERDUE_AMONG_COMPLETED -> {
                return handleOverdueAmongCompleted(payload);
            }
            case GET_COMPLETED_LAST_MONTH -> {
                return handleGetCompletedLastMonth(payload);
            }
            case GET_COMPLETED_LAST_WEEK -> {
                return handleGetCompletedLastWeek(payload);
            }
            default -> throw new UnsupportedOperationException("Message type is not related to statistics");
        }
    }

    private Reply<TrackCompletionDTO> handleGetTrackCompletion(String payload) {
        return exec.withErrorMapping(() ->
                Reply.ok(
                        StatisticsMessageType.TRACK_COMPLETION,
                        clickhouseService.getTrackCompletion(
                                IdParser.run(payload)
                        )
                )
        );
    }

    private Reply<?> handleGetCompletedLastMonth(String payload) {
        return exec.withErrorMapping(() ->
                Reply.ok(
                        StatisticsMessageType.COMPLETED_LAST_MONTH,
                        clickhouseService.getCompletedLastMonth(
                                IdParser.run(payload)
                        )
                )
        );
    }

    private Reply<?> handleGetCompletedLastWeek(String payload) {
        return exec.withErrorMapping(() ->
                Reply.ok(
                        StatisticsMessageType.COMPLETED_LAST_WEEK,
                        clickhouseService.getCompletedLastMonth(
                                IdParser.run(payload)
                        )
                )
        );
    }

    private Reply<?> handleOverdueAmongCompleted(String payload) {
        return exec.withErrorMapping(() ->
                Reply.ok(
                        StatisticsMessageType.OVERDUE_AMONG_COMPLETED,
                        clickhouseService.getOverdueAmongCompleted(
                                IdParser.run(payload)
                        )
                )
        );
    }

    private Reply<?> handleGetWorkInProgress(String payload) {
        return exec.withErrorMapping(() ->
                Reply.ok(
                        StatisticsMessageType.WORK_IN_PROGRESS,
                        clickhouseService.getWIP(
                                IdParser.run(payload)
                        )
                )
        );
    }

    private Reply<?> handleActiveTasksAging(String payload) {
        return exec.withErrorMapping(() ->
                Reply.ok(
                        StatisticsMessageType.ACTIVE_TASKS_AGING,
                        clickhouseService.getActiveTasksAging(
                                IdParser.run(payload)
                        )
                )
        );
    }

    private Reply<?> handleGetOverduePressure(String payload) {
        return exec.withErrorMapping(() ->
                Reply.ok(
                        StatisticsMessageType.OVERDUE_PRESSURE,
                        clickhouseService.getOverduePressure(
                                IdParser.run(payload)
                        )
                )
        );
    }

    private Reply<?> handleGetRemainingLoad(String payload) {
        return exec.withErrorMapping(() ->
                Reply.ok(
                        StatisticsMessageType.REMAINING_LOAD,
                        clickhouseService.getRemainingLoad(
                                IdParser.run(payload)
                        )
                )
        );
    }
}
