package com.grind.statistics.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grind.statistics.dto.CoreRecord;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.StringJoiner;

@Repository
@RequiredArgsConstructor
public class ClickhouseRepository {

    @Value("${clickhouse.baseUrl}")
    private String clickHouseUrl;
    private WebClient webClient;

    private final ObjectMapper objectMapper;

    @PostConstruct
    public void init(){
        this.webClient = WebClient.builder()
                .baseUrl(clickHouseUrl)
                .build();
    }

    public void postEvent(List<CoreRecord> batch) throws JsonProcessingException {

        if (batch.isEmpty()) return;
        StringJoiner joiner = new StringJoiner("\n");
        for (CoreRecord coreRecord : batch) {
            String s = objectMapper.writeValueAsString(coreRecord);
            joiner.add(s);
        }
        String body = joiner.toString();

        webClient.post()
                .uri(uriBuilder ->
                    uriBuilder
                            .queryParam("database", "analytics")
                            .queryParam("query", "INSERT INTO analytics_raw FORMAT JSONEachRow")
                            .queryParam("input_format_skip_unknown_fields", 1)
                            .build()

                )
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
