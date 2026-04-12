package com.grind.core.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grind.core.dto.entity.StatisticsEventDTO;
import com.grind.core.dto.entity.TaskDTO;
import com.grind.core.enums.CoreMessageType;
import com.grind.core.enums.TaskStatus;
import com.grind.core.model.OutboxEvent;
import com.grind.core.model.Task;
import com.grind.core.repository.OutboxRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Publishes outbox events to Kafka in scheduled batches.
 * Persists updated outbox status after delivery attempts.
 */
@Service
@RequiredArgsConstructor
public class OutboxService {
    private static final Logger log = LoggerFactory.getLogger(OutboxService.class);
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;
    private final KafkaProducer kafkaProducer;
    private final EntityManager entityManager;

    @Value("${kafka.topic.core.event.task}")
    private String coreEvTaskTopic;
    @Value("${kafka.topic.core.system.response}")
    private String coreSystemResTopic;
    @Value("${kafka.outbox-batch-size}")
    private Integer batchSize;

    // TODO add retry for 'FAILED' outbox events

    /**
     * Sends a batch of pending outbox events to Kafka.
     */
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
                        ev.getTopic(),
                        ev.getId()
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

    /**
     * Locks and returns the next batch of pending outbox events.
     *
     * @return list of events to send
     */
    @Transactional
    public List<OutboxEvent> fetchBatch() {
        return outboxRepository.lockBatch(batchSize);
    }

    /**
     * Persists updated event statuses after sending.
     *
     * @param events processed outbox events
     */
    @Transactional
    public void updateStatuses(List<OutboxEvent> events) {
        outboxRepository.saveAll(events);
    }

    /**
     * Stores a single outbox event derived from a task.
     *
     * @param dto     task payload
     * @param type    core message type
     * @param traceId tracing identifier
     */
    public void genEvent(TaskDTO dto, CoreMessageType type, String traceId) {
        outboxRepository.save(toOutbox(dto, type, traceId, resolveAuthenticatedUserId()));
    }

    /**
     * Stores multiple outbox events derived from tasks.
     *
     * @param dtoList task payloads
     * @param type    core message type
     * @param traceId tracing identifier
     */
    public void genEvents(List<TaskDTO> dtoList, CoreMessageType type, String traceId) {
        outboxRepository.saveAll(
                dtoList.stream()
                        .map(dto -> toOutbox(dto, type, traceId, resolveAuthenticatedUserId()))
                        .toList()
        );
    }

    /**
     * Stores multiple outbox events derived from managed task entities.
     * Uses the task owner's user id when no request-scoped authentication exists,
     * which is required for scheduled jobs like overdue marking.
     *
     * @param tasks   task entities
     * @param type    core message type
     * @param traceId tracing identifier
     */
    public void genEventsForTasks(List<Task> tasks, CoreMessageType type, String traceId) {
        outboxRepository.saveAll(
                tasks.stream()
                        .map(task -> toOutbox(task.mapDTO(), type, traceId, resolveTaskUserId(task)))
                        .toList()
        );
    }

    private OutboxEvent toOutbox(TaskDTO dto, CoreMessageType type, String traceId, String userId) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalStateException(
                    "Could not resolve user id for outbox event " + type + " of task " + dto.id()
            );
        }
        if (traceId == null || traceId.isBlank()) {
            throw new IllegalStateException(
                    "Could not resolve trace id for outbox event " + type + " of task " + dto.id()
            );
        }
        log.info(">>>>> USER ID: {}", userId);

        String sprintId = dto.sprint_id();
        TaskStatus taskStatus = dto.status();
        Long version = dto.version();
        if (type == CoreMessageType.TASK_DELETED) {
            sprintId = null;
            taskStatus = TaskStatus.DELETED;
            version = nextVersion(dto.version());
        }

        try {
            String payload = objectMapper.writeValueAsString(
                    new StatisticsEventDTO(
                            dto.track_id(),
                            sprintId,
                            userId,
                            dto.id(),
                            type == CoreMessageType.TASK_DELETED ? null : dto.plannedDate(),
                            version,
                            taskStatus,
                            LocalDateTime.now()
                    )
            );

            OutboxEvent ev = new OutboxEvent();
            ev.setAggregateId(dto.id());
            ev.setAggregateType("TASK");
            ev.setAggregateVersion(version);
            ev.setTopic(coreEvTaskTopic);
            ev.setEventType(type);
            ev.setPayload(payload);
            ev.setTraceId(traceId);

            return ev;

        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize OutboxRecord", e);
        }
    }

    private String resolveAuthenticatedUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication == null ? null : authentication.getName();
    }

    private String resolveTaskUserId(Task task) {
        if (task.getTrack() != null
                && task.getTrack().getUserId() != null
                && !task.getTrack().getUserId().isBlank()) {
            return task.getTrack().getUserId();
        }
        return resolveAuthenticatedUserId();
    }

    private Long nextVersion(Long current) {
        return current == null ? 1L : current + 1L;
    }

    public void resendEventsAfter(Long eventId) {
        List<OutboxEvent> eventsToRepublish = eventId == null ?
                outboxRepository.getAllEvents() :
                outboxRepository.getEventsAfter(eventId);

        if (eventsToRepublish.isEmpty()) {
            return;
        }

        for (OutboxEvent ev : eventsToRepublish) {
            try {
                kafkaProducer.publish(
                        ev.getPayload(),
                        ev.getEventType(),
                        ev.getTraceId(),
                        coreSystemResTopic,
                        ev.getId()
                );
                ev.markSent();
                log.info("OUTBOX EVENT RESENT");
            } catch (Exception ex) {
                ev.markFailed(ex.getMessage());
                log.warn(ex.getMessage());
            }
        }
    }
}
