package com.grind.statistics.service.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grind.statistics.dto.StatisticsEventDTO;
import com.grind.statistics.dto.TrackCompletionDTO;
import com.grind.statistics.repository.ClickhouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static com.grind.statistics.repository.ClickhouseQueries.Q_INGEST_EVENT;
import static com.grind.statistics.repository.ClickhouseQueries.Q_TRACK_COMPLETION;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClickhouseService {
    private final ClickhouseRepository repository;
    private final ObjectMapper objectMapper;

    public void postEvent(List<StatisticsEventDTO> batch) {
        repository.requestInsert(
                Q_INGEST_EVENT,
                Map.of(),
                batch
        ).block();
    }

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

}
