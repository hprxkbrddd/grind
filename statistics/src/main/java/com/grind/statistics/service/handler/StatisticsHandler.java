package com.grind.statistics.service.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grind.statistics.dto.IdDTO;
import com.grind.statistics.dto.StatisticsMessageType;
import com.grind.statistics.dto.TrackCompletionDTO;
import com.grind.statistics.dto.wrap.Reply;
import com.grind.statistics.service.application.ClickhouseService;
import com.grind.statistics.util.ActionReplyExecutor;
import com.grind.statistics.util.IdParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        return Reply.ok(null, null);
    }

    private Reply<?> handleOverdueAmongCompleted(String payload) {
        return Reply.ok(null, null);
    }

    private Reply<?> handleGetWorkInProgress(String payload) {
        return Reply.ok(null, null);
    }

    private Reply<?> handleActiveTasksAging(String payload) {
        return Reply.ok(null, null);
    }

    private Reply<?> handleGetOverduePressure(String payload) {
        return Reply.ok(null, null);
    }

    private Reply<?> handleGetRemainingLoad(String payload) {
        return Reply.ok(null, null);
    }
}
