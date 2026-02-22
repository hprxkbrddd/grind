package com.grind.statistics.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grind.statistics.dto.OutboxRecord;
import com.grind.statistics.dto.StatisticsEventDTO;
import com.grind.statistics.enums.CoreTaskResMsgType;
import com.grind.statistics.service.application.ClickhouseService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static com.grind.statistics.util.ConsumerHelper.authenticate;
import static com.grind.statistics.util.ConsumerHelper.headerAsString;

@Configuration
@RequiredArgsConstructor
public class KafkaCoreConsumer {

    private static final Logger log = LoggerFactory.getLogger(KafkaCoreConsumer.class);
    private final KafkaProducer kafkaProducer;
    private final ObjectMapper objectMapper;
    private final ClickhouseService service;
    private static final List<CoreTaskResMsgType> events = List.of(
            CoreTaskResMsgType.TASK_CREATED,
            CoreTaskResMsgType.TASK_DELETED,
            CoreTaskResMsgType.TASK_COMPLETED,
            CoreTaskResMsgType.TASK_PLANNED,
            CoreTaskResMsgType.TASK_AT_BACKLOG
    );

    @Value("${kafka.topic.core.event.task}")
    private String coreEvTaskTopic;

    @KafkaListener(containerFactory = "kafkaBatchListenerContainerFactory", topics = "${kafka.topic.core.event.task}")
    public void listenCore(
            List<ConsumerRecord<String, String>> records, Acknowledgment ack
    ) throws JsonProcessingException {
        List<StatisticsEventDTO> batch = new ArrayList<>();

        for (ConsumerRecord<String, String> rec : records) {
            try {
                String payload = rec.value();

                // SAFE HEADER PARSING
                String traceId = headerAsString(rec, "X-Trace-Id");
                String eventId = headerAsString(rec, "X-Event-Id");
                String roles = headerAsString(rec, "X-Roles");

                // PARSING PAYLOAD
                OutboxRecord msg = objectMapper.readValue(payload, OutboxRecord.class);

                // FORMING AUTHENTICATION OBJECT
                authenticate(msg.userId(), roles);

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

            } finally {
                SecurityContextHolder.clearContext();
            }
        }
        service.postEvent(batch);
        ack.acknowledge();
        log.info("INGESTED BATCH");
        for (StatisticsEventDTO r : batch) {
            log.info("CORE RECORD: \nevent_id={}\ntask_id={}", r.eventId(), r.taskId());
        }
    }
}
