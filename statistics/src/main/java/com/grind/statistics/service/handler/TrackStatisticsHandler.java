package com.grind.statistics.service.handler;

import com.grind.statistics.dto.response.track.TrackCompletionDTO;
import com.grind.statistics.dto.wrap.Reply;
import com.grind.statistics.enums.StatisticsMessageType;
import com.grind.statistics.service.application.TrackService;
import com.grind.statistics.util.ActionReplyExecutor;
import com.grind.statistics.util.IdParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TrackStatisticsHandler {

    private final TrackService trackService;
    private final ActionReplyExecutor exec;

    public Reply<?> routeReply(StatisticsMessageType type, String payload) {
        switch (type) {
            case GET_TRACK_COMPLETION -> {
                return handleGetTrackCompletion(payload);
            }
            case GET_TRACK_REMAINING_LOAD -> {
                return handleGetRemainingLoad(payload);
            }
            case GET_TRACK_OVERDUE_PRESSURE -> {
                return handleGetOverduePressure(payload);
            }
            case GET_TRACK_ACTIVE_TASKS_AGING -> {
                return handleActiveTasksAging(payload);
            }
            case GET_TRACK_WORK_IN_PROGRESS -> {
                return handleGetWorkInProgress(payload);
            }
            case GET_TRACK_OVERDUE_AMONG_COMPLETED -> {
                return handleOverdueAmongCompleted(payload);
            }
            case GET_COMPLETED_LAST_MONTH -> {
                return handleGetCompletedLastMonth(payload);
            }
            case GET_COMPLETED_LAST_WEEK -> {
                return handleGetCompletedLastWeek(payload);
            }
            default -> throw new UnsupportedOperationException("Message type is not related to track statistics");
        }
    }

    private Reply<TrackCompletionDTO> handleGetTrackCompletion(String payload) {
        return exec.withErrorMapping(() ->
                Reply.ok(
                        StatisticsMessageType.TRACK_COMPLETION,
                        trackService.getTrackCompletion(
                                IdParser.run(payload)
                        )
                )
        );
    }

    private Reply<?> handleGetCompletedLastMonth(String payload) {
        return exec.withErrorMapping(() ->
                Reply.ok(
                        StatisticsMessageType.COMPLETED_LAST_MONTH,
                        trackService.getCompletedLastMonth(
                                IdParser.run(payload)
                        )
                )
        );
    }

    private Reply<?> handleGetCompletedLastWeek(String payload) {
        return exec.withErrorMapping(() ->
                Reply.ok(
                        StatisticsMessageType.COMPLETED_LAST_WEEK,
                        trackService.getCompletedLastWeek(
                                IdParser.run(payload)
                        )
                )
        );
    }

    private Reply<?> handleOverdueAmongCompleted(String payload) {
        return exec.withErrorMapping(() ->
                Reply.ok(
                        StatisticsMessageType.TRACK_OVERDUE_AMONG_COMPLETED,
                        trackService.getOverdueAmongCompleted(
                                IdParser.run(payload)
                        )
                )
        );
    }

    private Reply<?> handleGetWorkInProgress(String payload) {
        return exec.withErrorMapping(() ->
                Reply.ok(
                        StatisticsMessageType.TRACK_WORK_IN_PROGRESS,
                        trackService.getWIP(
                                IdParser.run(payload)
                        )
                )
        );
    }

    private Reply<?> handleActiveTasksAging(String payload) {
        return exec.withErrorMapping(() ->
                Reply.ok(
                        StatisticsMessageType.TRACK_ACTIVE_TASKS_AGING,
                        trackService.getActiveTasksAging(
                                IdParser.run(payload)
                        )
                )
        );
    }

    private Reply<?> handleGetOverduePressure(String payload) {
        return exec.withErrorMapping(() ->
                Reply.ok(
                        StatisticsMessageType.TRACK_OVERDUE_PRESSURE,
                        trackService.getOverduePressure(
                                IdParser.run(payload)
                        )
                )
        );
    }

    private Reply<?> handleGetRemainingLoad(String payload) {
        return exec.withErrorMapping(() ->
                Reply.ok(
                        StatisticsMessageType.TRACK_REMAINING_LOAD,
                        trackService.getRemainingLoad(
                                IdParser.run(payload)
                        )
                )
        );
    }
}
