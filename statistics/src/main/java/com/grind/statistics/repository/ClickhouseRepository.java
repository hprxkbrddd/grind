package com.grind.statistics.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.Map;
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
        return webClient.get()
                .uri(uriBuilder -> buildUri(uriBuilder, query, params))
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError,
                        resp -> resp.bodyToMono(String.class)
                                .flatMap(msg -> Mono.error(new RuntimeException("ClickHouse 5xx: " + msg)))
                )
                .onStatus(HttpStatusCode::is4xxClientError,
                        resp -> resp.bodyToMono(String.class)
                                .flatMap(msg ->
                                        Mono.error(new IllegalStateException("ClickHouse 4xx: " + msg))
                                ))
                .bodyToFlux(expectedRes);
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
        return uriBuilder.build();
    }
}
