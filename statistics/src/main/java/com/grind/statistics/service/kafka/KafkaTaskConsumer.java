package com.grind.statistics.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grind.statistics.dto.CoreMessageType;
import com.grind.statistics.dto.CoreRecord;
import com.grind.statistics.repository.ClickhouseRepository;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class KafkaTaskConsumer {

    private final KafkaProducer kafkaProducer;
    private final ObjectMapper objectMapper;
    private final ClickhouseRepository repository;
    private static final List<CoreMessageType> events = List.of(
            CoreMessageType.TASK_CREATED,
            CoreMessageType.TASK_DELETED,
            CoreMessageType.TASK_COMPLETED,
            CoreMessageType.TASK_PLANNED,
            CoreMessageType.TASK_AT_BACKLOG
    );

    @Value("${kafka.topic.core.event.task}")
    private String coreEvTaskTopic;

    @KafkaListener(containerFactory = "kafkaListenerContainerFactory", topics = "${kafka.topic.core.event.task}")
    public void listenCore(
            List<ConsumerRecord<String, String>> records, Acknowledgment ack
    ) throws JsonProcessingException {
        List<CoreRecord> batch = new ArrayList<>();

        for (ConsumerRecord<String, String> rec : records) {
            try {
                String payload = rec.value();

                // SAFE HEADER PARSING
                String traceId = headerAsString(rec, "X-Trace-Id");
                String userId = headerAsString(rec, "X-User-Id");
                String roles = headerAsString(rec, "X-Roles");
                String messageType = headerAsString(rec, "X-Message-Type");

                // FORMING AUTHENTICATION OBJECT
                authenticate(userId, roles);

                // PARSING PAYLOAD



                // FILLING BATCH
                batch.add(new CoreRecord(
                                msg.payload().eventId(),
                                msg.payload().trackId(),
                                msg.payload().sprintId(),
                                msg.payload().taskId(),
                                msg.payload().version(),
                                msg.payload().taskStatus(),
                                msg.payload().changedAt()
                        )
                );

            } finally {
                SecurityContextHolder.clearContext();
            }
        }
        repository.postEvent(batch);
        ack.acknowledge();
    }

    @KafkaListener(id = "stats-server", topics = "statistics.request")
    public void listen(
            @Payload String payload,
            @Header(KafkaHeaders.CORRELATION_ID) String correlationId,
            @Header("X-Trace-Id") String traceId,
            @Header("X-User-Id") String userId,
            @Header(value = "X-Roles", required = false) String roles
    ) {
        // FORMING AUTHENTICATION OBJECT
        authenticate(userId, roles);

        // CODE
        // ...
        // CODE
    }

    private void authenticate(String userId, String roles) {
        List<SimpleGrantedAuthority> authorities = Arrays.stream(
                        roles != null ? roles.split(",") : new String[0]
                )
                .map(String::trim)
                .filter(r -> !r.isBlank())
                .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                .map(SimpleGrantedAuthority::new)
                .toList();

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userId, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private static String headerAsString(ConsumerRecord<?, ?> rec, String name) {
        var h = rec.headers().lastHeader(name);
        return (h == null || h.value() == null) ? null : new String(h.value(), StandardCharsets.UTF_8);
    }
}
