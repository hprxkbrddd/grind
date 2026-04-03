package com.grind.statistics.service.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grind.statistics.dto.request.StatCoreSyncDTO;
import com.grind.statistics.service.kafka.KafkaProducer;
import com.grind.statistics.util.TraceContext;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SynchronizationService {

    private static final Logger log = LoggerFactory.getLogger(SynchronizationService.class);
    private final QueryService queryService;
    private final KafkaProducer kafkaProducer;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topic.core.system.request}")
    public String coreSystemRequestTopic;

    public void synchronizeDatabases(){
        Long eventId = queryService.getLastEventId();
        try {
            kafkaProducer.publish(
                    objectMapper.writeValueAsString(new StatCoreSyncDTO(eventId)),
                    TraceContext.getTraceId(),
                    coreSystemRequestTopic
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e); // TODO introduce better handling
        }
    }
}
