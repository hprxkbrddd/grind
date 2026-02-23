package com.grind.statistics.service.application;

import com.grind.statistics.dto.response.track.*;
import com.grind.statistics.repository.ClickhouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.grind.statistics.repository.TrackQueries.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class TrackService {
    private final ClickhouseRepository repository;

    public TrackCompletionDTO getTrackCompletion(String trackId) {
        List<TrackCompletionDTO> list = repository.requestSelect(
                Q_TRACK_COMPLETION,
                Map.of("param_track", trackId),
                TrackCompletionDTO.class
        ).collectList().block();

        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("track id is not in stats db");
        }

        return list.get(0);
    }

    public TrackRemainingLoadDTO getRemainingLoad(String trackId) {
        List<TrackRemainingLoadDTO> list = repository.requestSelect(
                Q_TRACK_REMAINING_LOAD,
                Map.of("param_track", trackId),
                TrackRemainingLoadDTO.class
        ).collectList().block();

        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("track id is not in stats db");
        }

        return list.get(0);
    }

    public TrackOverduePressureDTO getOverduePressure(String trackId) {
        List<TrackOverduePressureDTO> list = repository.requestSelect(
                Q_TRACK_OVERDUE_PRESSURE,
                Map.of("param_track", trackId),
                TrackOverduePressureDTO.class
        ).collectList().block();

        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("track id is not in stats db");
        }

        return list.get(0);
    }

    public TrackActiveTasksAgingDTO getActiveTasksAging(String trackId) {
        List<TrackActiveTasksAgingDTO> list = repository.requestSelect(
                Q_TRACK_ACTIVE_TASKS_AGING,
                Map.of("param_track", trackId),
                TrackActiveTasksAgingDTO.class
        ).collectList().block();

        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("track id is not in stats db");
        }

        return list.get(0);
    }

    public TrackWIPDTO getWIP(String trackId) {
        List<TrackWIPDTO> list = repository.requestSelect(
                Q_TRACK_WORK_IN_PROGRESS,
                Map.of("param_track", trackId),
                TrackWIPDTO.class
        ).collectList().block();

        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("track id is not in stats db");
        }

        return list.get(0);
    }

    public TrackOverdueAmongCompletedDTO getOverdueAmongCompleted(String trackId) {
        List<TrackOverdueAmongCompletedDTO> list = repository.requestSelect(
                Q_TRACK_OVERDUE_AMONG_COMPLETED,
                Map.of("param_track", trackId),
                TrackOverdueAmongCompletedDTO.class
        ).collectList().block();

        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("track id is not in stats db");
        }

        return list.get(0);
    }

    public TrackCompletedLastMonthDTO getCompletedLastMonth(String trackId) {
        List<TrackCompletedLastMonthDTO> list = repository.requestSelect(
                Q_COMPLETED_LAST_MONTH,
                Map.of("param_track", trackId),
                TrackCompletedLastMonthDTO.class
        ).collectList().block();

        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("track id is not in stats db");
        }

        return list.get(0);
    }

    public TrackCompletedLastMonthDTO getCompletedLastWeek(String trackId) {
        List<TrackCompletedLastMonthDTO> list = repository.requestSelect(
                Q_COMPLETED_LAST_WEEK,
                Map.of("param_track", trackId),
                TrackCompletedLastMonthDTO.class
        ).collectList().block();

        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("track id is not in stats db");
        }

        return list.get(0);
    }
}
