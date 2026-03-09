package com.grind.statistics.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grind.statistics.dto.response.ddl.CountResponse;
import com.grind.statistics.dto.response.ddl.DdlResponse;
import com.grind.statistics.dto.response.ddl.DescribeTableResponse;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;

import static com.grind.statistics.repository.ClickhouseQueries.ANALYTICS_DB;

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

    @PostConstruct
    public void init() {
        this.webClient = WebClient.builder()
                .baseUrl(clickHouseUrl)
                .defaultHeaders(h -> h.setBasicAuth(chUsername, chPassword))
                .build();
    }

    public Mono<Void> requestInsert(
            String query,
            Map<String, String> params,
            List<? extends Record> payload
    ) {
        return Mono.defer(() -> {
            try {
                String body = buildBody(payload);

                return webClient.post()
                        .uri(uriBuilder -> buildUri(uriBuilder, query, params))
                        .contentType(MediaType.TEXT_PLAIN)
                        .bodyValue(body)
                        .retrieve()
                        .onStatus(HttpStatusCode::is5xxServerError,
                                resp -> {
                                    log.error(body);
                                    return resp.bodyToMono(String.class)
                                            .flatMap(msg -> Mono.error(new RuntimeException("ClickHouse 5xx: " + msg)));
                                }
                        )
                        .onStatus(HttpStatusCode::is4xxClientError,
                                resp -> {
                                    log.error(body);
                                    return resp.bodyToMono(String.class)
                                            .flatMap(msg -> Mono.error(new IllegalStateException("ClickHouse 4xx: " + msg)));
                                }
                        )
                        .toBodilessEntity()
                        .then();

            } catch (JsonProcessingException e) {
                return Mono.error(new IllegalStateException("Serialization failed", e));
            }
        });
    }

    public <T> Flux<T> requestSelect(
            String query,
            Map<String, String> params,
            Class<T> expectedRes
    ) {
        return webClient.post()
                .uri(uriBuilder -> {
                    uriBuilder.queryParam("database", "analytics");
                    if (params != null) params.forEach(uriBuilder::queryParam);
                    return uriBuilder.build();
                })
                .bodyValue(query)
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError,
                        resp -> resp.bodyToMono(String.class)
                                .flatMap(msg -> Mono.error(new RuntimeException("ClickHouse 5xx: " + msg))))
                .onStatus(HttpStatusCode::is4xxClientError,
                        resp -> resp.bodyToMono(String.class)
                                .flatMap(msg -> Mono.error(new IllegalStateException("ClickHouse 4xx: " + msg))))
                .bodyToFlux(expectedRes);
    }

    public Mono<Boolean> databaseExists(String databaseName) {
        String query = """
                SELECT count() AS cnt
                FROM system.databases
                WHERE name = '%s'
                FORMAT JSONEachRow
                """.formatted(databaseName);
        return existsByQuery(query);
    }

    public Mono<Boolean> tableExists(String databaseName, String tableName) {
        String query = """
                SELECT count() AS cnt
                FROM system.tables
                WHERE database = '%s'
                  AND name = '%s'
                FORMAT JSONEachRow
                """.formatted(databaseName, tableName);
        return existsByQuery(query);
    }

    public Mono<Boolean> tableExists(String tableName) {
        return tableExists(ANALYTICS_DB, tableName);
    }

    public Mono<String> fetchDatabaseDdl(String databaseName) {
        String query = "SHOW CREATE DATABASE " + databaseName + " FORMAT JSONEachRow";
        return fetchDdl(query);
    }

    public Mono<String> fetchTableDdl(String databaseName, String tableName) {
        String query = "SHOW CREATE TABLE " + databaseName + "." + tableName + " FORMAT JSONEachRow";
        return fetchDdl(query);
    }

    public Mono<Void> executeStatement(String statement) {
        return executeSql(statement)
                .toBodilessEntity()
                .then();
    }

    public Flux<DescribeTableResponse> describeTable(String databaseName, String tableName) {
        String query = "DESCRIBE TABLE " + databaseName + "." + tableName + " FORMAT JSONEachRow";
        return executeSql(query)
                .bodyToFlux(DescribeTableResponse.class);
    }

    private Mono<String> fetchDdl(String query) {
        return executeSql(query)
                .bodyToMono(String.class)
                .flatMap(body -> extractDdl(body)
                        .map(Mono::just)
                        .orElseGet(Mono::empty));
    }

    private Mono<Boolean> existsByQuery(String query) {
        return executeSql(query)
                .bodyToMono(String.class)
                .map(this::extractCount)
                .map(count -> count > 0);
    }

    private WebClient.ResponseSpec executeSql(String sql) {
        return webClient.post()
                .uri(UriBuilder::build)
                .contentType(MediaType.TEXT_PLAIN)
                .bodyValue(sql)
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError,
                        resp -> resp.bodyToMono(String.class)
                                .flatMap(msg -> Mono.error(new RuntimeException("ClickHouse 5xx: " + msg))))
                .onStatus(HttpStatusCode::is4xxClientError,
                        resp -> resp.bodyToMono(String.class)
                                .flatMap(msg -> Mono.error(new IllegalStateException("ClickHouse 4xx: " + msg))));
    }

    private long extractCount(String responseBody) {
        String payload = responseBody.lines()
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Empty ClickHouse response for exists query"));

        try {
            return objectMapper.readValue(payload, CountResponse.class).cnt();
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to parse ClickHouse exists response", e);
        }
    }

    private Optional<String> extractDdl(String responseBody) {
        return responseBody.lines()
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .map(line -> {
                    try {
                        return objectMapper.readValue(line, DdlResponse.class).statement();
                    } catch (JsonProcessingException e) {
                        throw new IllegalStateException("Failed to parse ClickHouse DDL response", e);
                    }
                })
                .findFirst();
    }

    private String buildBody(List<? extends Record> payload) throws JsonProcessingException {
        String body = "";
        if (payload != null && !payload.isEmpty()) {
            StringJoiner joiner = new StringJoiner("\n");
            for (Record statEv : payload) {
                joiner.add(objectMapper.writeValueAsString(statEv));
            }
            body = joiner + "\n";
        }
        return body;
    }

    private URI buildUri(
            UriBuilder uriBuilder,
            String query,
            Map<String, String> params
    ) {
        uriBuilder.queryParam("query", query);
        if (params != null && !params.isEmpty()) {
            params.forEach(uriBuilder::queryParam);
        }
        log.info(uriBuilder.toUriString());
        return uriBuilder.build();
    }
}
