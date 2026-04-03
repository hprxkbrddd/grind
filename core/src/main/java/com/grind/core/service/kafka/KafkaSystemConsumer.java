package com.grind.core.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grind.core.dto.request.StatCoreSyncDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaSystemConsumer {

    private final OutboxService outboxService;
    private final ObjectMapper objectMapper;

    @KafkaListener(id = "core-server-system", topics = "core.system.request")
    public void listen(
            @Payload String payload
    ) {
        StatCoreSyncDTO body;
        try {
            body = objectMapper.readValue(payload, StatCoreSyncDTO.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Provided payload could not be converted to StatCoreSyncDTO");
        }

        outboxService.resendEventsAfter(body.lastIngestedEventId());
    }
}
