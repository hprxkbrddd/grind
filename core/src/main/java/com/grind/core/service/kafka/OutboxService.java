package com.grind.core.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grind.core.dto.entity.OutboxRecord;
import com.grind.core.dto.entity.TaskDTO;
import com.grind.core.enums.CoreMessageType;
import com.grind.core.model.OutboxEvent;
import com.grind.core.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OutboxService {
    private static final Logger log = LoggerFactory.getLogger(OutboxService.class);
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;
    private final KafkaProducer kafkaProducer;

    @Value("${kafka.topic.core.event.task}")
    private String coreEvTaskTopic;
    @Value("${kafka.outbox-batch-size}")
    private Integer batchSize;

    // TODO add retry for 'FAILED' outbox events

    @Scheduled(fixedDelay = 1000)
    public void sendOutbox() {

        List<OutboxEvent> batch = fetchBatch();

        if (batch.isEmpty()) {
            return;
        }

        for (OutboxEvent ev : batch) {
            try {
                kafkaProducer.publish(
                        ev.getPayload(),
                        ev.getEventType(),
                        ev.getTraceId(),
                        ev.getTopic()
                );
                ev.markSent();
                log.info("OUTBOX EVENT SENT");
            } catch (Exception ex) {
                ev.markFailed(ex.getMessage());
                log.warn(ex.getMessage());
            }
        }

        updateStatuses(batch);
    }

    @Transactional
    public List<OutboxEvent> fetchBatch() {
        return outboxRepository.lockBatch(batchSize);
    }

    @Transactional
    public void updateStatuses(List<OutboxEvent> events) {
        outboxRepository.saveAll(events);
    }

    public void genEvent(TaskDTO dto, CoreMessageType type, String traceId) {
        outboxRepository.save(toEvent(dto, type, traceId));
    }

    public void genEvents(List<TaskDTO> dtoList, CoreMessageType type, String traceId) {
        outboxRepository.saveAll(
                dtoList.stream()
                        .map(dto -> toEvent(dto, type, traceId))
                        .toList()
        );
    }

    private OutboxEvent toEvent(TaskDTO dto, CoreMessageType type, String traceId) {

        try {
            String payload = objectMapper.writeValueAsString(
                    new OutboxRecord(
                            dto.track_id(),
                            dto.sprint_id(),
                            dto.id(),
                            dto.version(),
                            dto.status(),
                            Instant.now()
                    )
            );

            OutboxEvent ev = new OutboxEvent();
            ev.setAggregateId(dto.id());
            ev.setAggregateType("TASK");
            ev.setAggregateVersion(dto.version());
            ev.setTopic(coreEvTaskTopic);
            ev.setEventType(type);
            ev.setPayload(payload);
            ev.setTraceId(traceId);

            return ev;

        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize OutboxRecord", e);
        }
    }
}
