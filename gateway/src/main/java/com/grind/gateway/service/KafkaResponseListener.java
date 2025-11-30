package com.grind.gateway.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class KafkaResponseListener {
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    KafkaResponseListener(){
        this.webClient = WebClient.builder()
                .baseUrl("/frontend/response/uri")
                .build();
        this.objectMapper = new ObjectMapper();
    }

    @KafkaListener(id = "gateway", topics = "response")
    public void sendResponse(
            @Payload String payload,
            @Header(KafkaHeaders.CORRELATION_ID) String correlationId
    ) throws JsonProcessingException {
        Object response = objectMapper.readValue(payload, Object.class);
        webClient.post()
                .bodyValue(response)
                .header("X-Correlation-Id", correlationId);
    }
}
