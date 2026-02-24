package com.grind.statistics.service.handler;

import com.grind.statistics.dto.response.sprint.SprintStatsDTO;
import com.grind.statistics.dto.response.track.TrackActualStateStatsDTO;
import com.grind.statistics.dto.response.track.TrackRawStatsDTO;
import com.grind.statistics.dto.wrap.Reply;
import com.grind.statistics.enums.StatisticsMessageType;
import com.grind.statistics.service.application.QueryService;
import com.grind.statistics.util.ActionReplyExecutor;
import com.grind.statistics.util.IdParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatisticsHandler {

    private final QueryService queryService;
    private final ActionReplyExecutor exec;

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
}
