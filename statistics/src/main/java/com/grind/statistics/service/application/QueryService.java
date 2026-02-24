package com.grind.statistics.service.application;

import com.grind.statistics.dto.request.StatisticsEventDTO;
import com.grind.statistics.dto.response.sprint.SprintStatsDTO;
import com.grind.statistics.dto.response.track.*;
import com.grind.statistics.repository.ClickhouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.grind.statistics.repository.ClickhouseQueries.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class QueryService {
    private final ClickhouseRepository repository;

    public TrackActualStateStatsDTO getActualStateStats(String trackId){
        List<TrackActualStateStatsDTO> list = repository.requestSelect(
                Q_TRACK_STATS_ACTUAL_STATE,
                Map.of("param_track", trackId),
                TrackActualStateStatsDTO.class
        ).collectList().block();

        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("track id is not in stats db");
        }

        return list.get(0);
    }
    public TrackRawStatsDTO getRawStats(String trackId){
        List<TrackRawStatsDTO> list = repository.requestSelect(
                Q_TRACK_STATS_RAW,
                Map.of("param_track", trackId),
                TrackRawStatsDTO.class
        ).collectList().block();

        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("track id is not in stats db");
        }

        return list.get(0);
    }

    public SprintStatsDTO getSprintStats(String sprintId) {
        List<SprintStatsDTO> list = repository.requestSelect(
                Q_SPRINT_STATS,
                Map.of("param_sprint", sprintId),
                SprintStatsDTO.class
        ).collectList().block();

        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("sprint id is not in stats db");
        }

        return list.get(0);
    }

    public void postEvent(List<StatisticsEventDTO> batch) {
        repository.requestInsert(
                Q_INGEST_EVENT,
                Map.of(),
                batch
        ).block();
    }
}
