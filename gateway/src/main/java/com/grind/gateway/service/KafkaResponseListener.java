package com.grind.gateway.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
public class KafkaResponseListener {
    private final ObjectMapper objectMapper;
    private final PendingRegistry pendingRegistry;

    @KafkaListener(id = "gateway", topics = "response")
    public void handleResponse(
            @Payload String payload,
            @Header(KafkaHeaders.CORRELATION_ID) String correlationId
    ) throws JsonProcessingException {
        Object response = objectMapper.readValue(payload, Object.class);
        CompletableFuture<Object> future = pendingRegistry.remove(correlationId);
        if (future!=null){
            future.complete(response);
        } else {
            System.out.println(">>>>>> I'M NOT YOUR BRAH");
        }
    }
}
