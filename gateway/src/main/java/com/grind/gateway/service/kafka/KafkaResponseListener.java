package com.grind.gateway.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grind.gateway.dto.Body;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

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
        Body response = objectMapper.readValue(payload, Body.class);
        CompletableFuture<Body> future = pendingRegistry.remove(correlationId);
        if (future!=null){
            future.complete(response);
        } else {
            System.out.println(">>>>>> I'M NOT YOUR BRAH");
        }
    }
}
