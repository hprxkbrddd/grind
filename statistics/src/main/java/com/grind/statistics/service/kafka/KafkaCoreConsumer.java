package com.grind.statistics.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grind.statistics.dto.request.OutboxRecord;
import com.grind.statistics.dto.request.StatisticsEventDTO;
import com.grind.statistics.enums.CoreMessageType;
import com.grind.statistics.service.application.GenericService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static com.grind.statistics.util.ConsumerHelper.headerAsString;

@Configuration
@RequiredArgsConstructor
public class KafkaCoreConsumer {

    private static final Logger log = LoggerFactory.getLogger(KafkaCoreConsumer.class);
    private final KafkaProducer kafkaProducer;
    private final ObjectMapper objectMapper;
    private final GenericService service;
    private static final List<CoreMessageType> events = List.of(
            CoreMessageType.TASK_CREATED,
            CoreMessageType.TASK_DELETED,
            CoreMessageType.TASK_COMPLETED,
            CoreMessageType.TASK_PLANNED,
            CoreMessageType.TASK_AT_BACKLOG
    );

    @Value("${kafka.topic.core.event.task}")
    private String coreEvTaskTopic;

    @KafkaListener(containerFactory = "kafkaBatchListenerContainerFactory", topics = "${kafka.topic.core.event.task}")
    public void listenCore(
            List<ConsumerRecord<String, String>> records, Acknowledgment ack
    ) throws JsonProcessingException {
        List<StatisticsEventDTO> batch = new ArrayList<>();

        for (ConsumerRecord<String, String> rec : records) {
            String payload = rec.value();

            // SAFE HEADER PARSING
            String eventId = headerAsString(rec, "X-Event-Id");

            // PARSING PAYLOAD
            OutboxRecord msg = objectMapper.readValue(payload, OutboxRecord.class);

            // FILLING BATCH
            batch.add(new StatisticsEventDTO(
                            eventId,
                            msg.trackId(),
                            msg.sprintId(),
                            msg.userId(),
                            msg.taskId(),
                            msg.version(),
                            msg.taskStatus(),
                            msg.changedAt().truncatedTo(ChronoUnit.MILLIS)
                    )
            );
        }
        service.postEvent(batch);
        ack.acknowledge();
        log.info("INGESTED BATCH");
        for (StatisticsEventDTO r : batch) {
            log.info("CORE RECORD: event_id={}; task_id={}; version={}; status={}",
                    r.eventId(), r.taskId(), r.version(), r.taskStatus());
        }
    }
}
