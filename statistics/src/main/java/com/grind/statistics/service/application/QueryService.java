package com.grind.statistics.service.application;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.grind.statistics.dto.request.StatisticsEventDTO;
import com.grind.statistics.dto.response.diagram.DiagramDTO;
import com.grind.statistics.dto.response.diagram.DiagramUnit;
import com.grind.statistics.dto.response.sprint.SprintStatsDTO;
import com.grind.statistics.dto.response.track.TrackActualStateStatsDTO;
import com.grind.statistics.dto.response.track.TrackRawStatsDTO;
import com.grind.statistics.repository.ClickhouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.grind.statistics.repository.ClickhouseQueries.*;

/**
 * Provides ClickHouse query and ingest operations for statistics data.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class QueryService {
    private final ClickhouseRepository repository;

    public TrackActualStateStatsDTO getActualStateStats(String trackId) {
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

    public TrackRawStatsDTO getRawStats(String trackId) {
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

    public DiagramDTO getDiagramDataPerDay(String trackId) {
        List<DiagramUnit> list = repository.requestSelect(
                Q_STATS_PER_DAY,
                Map.of("param_track", trackId),
                DiagramUnit.class
        ).collectList().block();

        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("track id is not in stats db");
        }

        return new DiagramDTO(list);
    }

    public DiagramDTO getDiagramDataPerWeek(String trackId) {
        List<DiagramUnit> list = repository.requestSelect(
                Q_STATS_PER_WEEK,
                Map.of("param_track", trackId),
                DiagramUnit.class
        ).collectList().block();

        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("track id is not in stats db");
        }

        return new DiagramDTO(list);
    }

    public DiagramDTO getDiagramDataPerDayInRange(String trackId, LocalDate startDate, LocalDate endDate) {
        if (isInvalidRange(startDate, endDate)) {
            log.debug("Invalid day range for track {}. Falling back to non-range diagram query", trackId);
            return getDiagramDataPerDay(trackId);
        }

        List<DiagramUnit> list = repository.requestSelect(
                Q_STATS_PER_DAY_IN_RANGE,
                Map.of(
                        "param_track", trackId,
                        "param_startDate", startDate.toString(),
                        "param_endDate", endDate.toString()
                ),
                DiagramUnit.class
        ).collectList().block();

        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("track id is not in stats db");
        }

        return new DiagramDTO(list);
    }

    public DiagramDTO getDiagramDataPerWeekInRange(String trackId, LocalDate startDate, LocalDate endDate) {
        if (isInvalidRange(startDate, endDate)) {
            log.debug("Invalid week range for track {}. Falling back to non-range diagram query", trackId);
            return getDiagramDataPerWeek(trackId);
        }

        List<DiagramUnit> list = repository.requestSelect(
                Q_STATS_PER_WEEK_IN_RANGE,
                Map.of(
                        "param_track", trackId,
                        "param_startDate", startDate.toString(),
                        "param_endDate", endDate.toString()
                ),
                DiagramUnit.class
        ).collectList().block();

        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("track id is not in stats db");
        }

        return new DiagramDTO(list);
    }

    private boolean isInvalidRange(LocalDate startDate, LocalDate endDate) {
        return startDate == null || endDate == null || startDate.isAfter(endDate);
    }

    public void postEvent(List<StatisticsEventDTO> batch) {
        repository.requestInsert(
                Q_INGEST_EVENT,
                Map.of(),
                batch
        ).block();
    }

    public Long getLastEventId(){
        LastEventIdRow row = repository.requestSelect(
                Q_LAST_EVENT,
                Map.of(),
                LastEventIdRow.class
        ).next().block();

        if (row == null || row.eventId() == null) {
            return null;
        }

        return row.eventId();
    }

    private record LastEventIdRow(
            @JsonProperty("event_id")
            Long eventId
    ) {
    }
}
