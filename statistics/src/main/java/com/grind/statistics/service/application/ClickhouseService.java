package com.grind.statistics.service.application;

import com.grind.statistics.dto.StatisticsEventDTO;
import com.grind.statistics.dto.TrackCompletionDTO;
import com.grind.statistics.repository.ClickhouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static com.grind.statistics.repository.ClickhouseQueries.Q_INGEST_EVENT;
import static com.grind.statistics.repository.ClickhouseQueries.Q_TRACK_COMPLETION;

@Service
@RequiredArgsConstructor
public class ClickhouseService {
    private final ClickhouseRepository repository;

    public void postEvent(List<StatisticsEventDTO> batch) {
        repository.requestInsert(
                Q_INGEST_EVENT,
                Map.of(),
                batch
        ).block();
    }

    public List<TrackCompletionDTO> getTrackCompletion(String trackId) {
        Mono<List<TrackCompletionDTO>> monoList = repository.requestSelect(
                Q_TRACK_COMPLETION,
                Map.of("track", trackId),
                TrackCompletionDTO.class
        ).collectList();
        return monoList.block();
    }
}
