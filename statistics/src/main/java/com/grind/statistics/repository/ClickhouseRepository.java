package com.grind.statistics.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grind.statistics.dto.CoreRecord;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.StringJoiner;

@Repository
@RequiredArgsConstructor
public class ClickhouseRepository {

    private static final Logger log = LoggerFactory.getLogger(ClickhouseRepository.class);
    @Value("${clickhouse.baseUrl}")
    private String clickHouseUrl;
    @Value("${clickhouse.username}")
    private String chUsername;
    @Value("${clickhouse.password}")
    private String chPassword;
    private WebClient webClient;

    private final ObjectMapper objectMapper;

    private static final String trackCompletionPercentQuery = """
            SELECT
                track_id,
                round(
                    countIf(task_status = 'COMPLETED') / count() * 100,
                    2
                ) AS completion_percent,
                count() AS total_tasks
            FROM analytics.task_actual_state_v
            WHERE track_id = {track:UInt64}
            GROUP BY track_id
            FORMAT JSONEachRow
            """;

    @PostConstruct
    public void init() {
        this.webClient = WebClient.builder()
                .baseUrl(clickHouseUrl)
                .defaultHeaders(h -> h.setBasicAuth(chUsername, chPassword))
                .build();
    }

    public void postEvent(List<CoreRecord> batch) throws JsonProcessingException {
        if (batch.isEmpty()) return;
        StringJoiner joiner = new StringJoiner("\n");
        for (CoreRecord coreRecord : batch) {
            joiner.add(objectMapper.writeValueAsString(coreRecord));
        }
        String body = joiner + "\n";

        log.info(body);

        webClient.post()
                .uri(uriBuilder ->
                        uriBuilder
                                .queryParam("database", "analytics")
                                .queryParam("query", "INSERT INTO raw FORMAT JSONEachRow\n")
                                .queryParam("input_format_skip_unknown_fields", 1)
                                .build()

                )
                .contentType(MediaType.TEXT_PLAIN)
                .bodyValue(body)
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError,
                        resp -> resp.bodyToMono(String.class)
                                .map(msg -> new RuntimeException("ClickHouse 5xx: " + msg)))
                .onStatus(HttpStatusCode::is4xxClientError,
                        resp -> resp.bodyToMono(String.class)
                                .map(msg -> new IllegalStateException("ClickHouse 4xx: " + msg)))
                .toBodilessEntity()
                .block();
    }
}
