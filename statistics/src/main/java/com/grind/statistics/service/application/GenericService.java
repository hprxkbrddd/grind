package com.grind.statistics.service.application;

import com.grind.statistics.dto.request.StatisticsEventDTO;
import com.grind.statistics.repository.ClickhouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.grind.statistics.repository.GenericQueries.Q_INGEST_EVENT;

@Service
@Slf4j
@RequiredArgsConstructor
public class GenericService {
    private final ClickhouseRepository repository;

    public void postEvent(List<StatisticsEventDTO> batch) {
        repository.requestInsert(
                Q_INGEST_EVENT,
                Map.of(),
                batch
        ).block();
    }
}
