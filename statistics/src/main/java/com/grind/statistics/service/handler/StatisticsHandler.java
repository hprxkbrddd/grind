package com.grind.statistics.service.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grind.statistics.dto.request.DiagramRangeRequestDTO;
import com.grind.statistics.dto.response.diagram.DiagramDTO;
import com.grind.statistics.dto.response.sprint.SprintStatsDTO;
import com.grind.statistics.dto.response.track.TrackActualStateStatsDTO;
import com.grind.statistics.dto.response.track.TrackRawStatsDTO;
import com.grind.statistics.dto.wrap.Reply;
import com.grind.statistics.enums.StatisticsMessageType;
import com.grind.statistics.service.application.QueryService;
import com.grind.statistics.service.application.SynchronizationService;
import com.grind.statistics.util.ActionReplyExecutor;
import com.grind.statistics.util.IdParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Routes statistics Kafka requests to query operations.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class StatisticsHandler {

    private final QueryService queryService;
    private final SynchronizationService synchronizationService;
    private final ActionReplyExecutor exec;
    private final ObjectMapper objectMapper;

    public Reply<?> routeReply(StatisticsMessageType type, String payload) {
        switch (type) {
            case GET_TRACK_STATS_ACTUAL_STATE -> {
                return handleGetTrackStatsActualState(payload);
            }
            case GET_TRACK_STATS_RAW -> {
                return handleGetTrackStatsRaw(payload);
            }
            case GET_SPRINT_STATS -> {
                return handleGetSprintStats(payload);
            }
            case GET_STATS_PER_WEEK -> {
                return handleGetStatsPerWeek(payload);
            }
            case GET_STATS_PER_DAY -> {
                return handleGetStatsPerDay(payload);
            }
            case GET_STATS_PER_WEEK_IN_RANGE -> {
                return handleGetStatsPerWeekInRange(payload);
            }
            case GET_STATS_PER_DAY_IN_RANGE -> {
                return handleGetStatsPerDayInRange(payload);
            }
            case SYNC_DATABASES -> {
                return handleSync();
            }
            default -> throw new UnsupportedOperationException("Message type is not related to track statistics");
        }
    }

    private Reply<TrackActualStateStatsDTO> handleGetTrackStatsActualState(String payload) {
        return exec.withErrorMapping(() ->
                Reply.ok(
                        StatisticsMessageType.TRACK_STATS_ACTUAL_STATE,
                        queryService.getActualStateStats(
                                IdParser.run(payload)
                        )
                )
        );
    }

    private Reply<TrackRawStatsDTO> handleGetTrackStatsRaw(String payload) {
        return exec.withErrorMapping(() ->
                Reply.ok(
                        StatisticsMessageType.TRACK_STATS_RAW,
                        queryService.getRawStats(
                                IdParser.run(payload)
                        )
                )
        );
    }

    private Reply<SprintStatsDTO> handleGetSprintStats(String payload) {
        return exec.withErrorMapping(() ->
                Reply.ok(
                        StatisticsMessageType.SPRINT_STATS,
                        queryService.getSprintStats(
                                IdParser.run(payload)
                        )
                )
        );
    }

    private Reply<DiagramDTO> handleGetStatsPerWeek(String payload) {
        return exec.withErrorMapping(() ->
                Reply.ok(
                        StatisticsMessageType.STATS_PER_WEEK,
                        queryService.getDiagramDataPerWeek(
                                IdParser.run(payload)
                        )
                )
        );
    }

    private Reply<DiagramDTO> handleGetStatsPerDay(String payload) {
        return exec.withErrorMapping(() ->
                Reply.ok(
                        StatisticsMessageType.STATS_PER_DAY,
                        queryService.getDiagramDataPerDay(
                                IdParser.run(payload)
                        )
                )
        );
    }

    private Reply<DiagramDTO> handleGetStatsPerWeekInRange(String payload) {
        return exec.withErrorMapping(() -> {
                    DiagramRangeRequestDTO request = objectMapper.readValue(payload, DiagramRangeRequestDTO.class);
                    return Reply.ok(
                            StatisticsMessageType.STATS_PER_WEEK_IN_RANGE,
                            queryService.getDiagramDataPerWeekInRange(
                                    request.trackId(),
                                    request.startDate(),
                                    request.endDate()
                            )
                    );
                }
        );
    }

    private Reply<DiagramDTO> handleGetStatsPerDayInRange(String payload) {
        return exec.withErrorMapping(() -> {
                    DiagramRangeRequestDTO request = objectMapper.readValue(payload, DiagramRangeRequestDTO.class);
                    return Reply.ok(
                            StatisticsMessageType.STATS_PER_DAY_IN_RANGE,
                            queryService.getDiagramDataPerDayInRange(
                                    request.trackId(),
                                    request.startDate(),
                                    request.endDate()
                            )
                    );
                }
        );
    }

    private Reply<?> handleSync() {
        return exec.withErrorMapping(() -> {
                    synchronizationService.synchronizeDatabases();
                    return Reply.ok(
                            StatisticsMessageType.DATABASES_SYNCED,
                            "CHIWAPCHICHI"
                    );
                }
        );
    }
}
