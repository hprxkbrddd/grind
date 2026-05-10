package com.grind.template.service.kafka;

import com.grind.template.enums.TemplateMessageType;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Produces Kafka messages for core responses and events.
 * Adds trace, user, and role headers from the security context.
 */
@Service
@RequiredArgsConstructor
public class KafkaProducer {
    private static final Logger log = LoggerFactory.getLogger(KafkaProducer.class);
    @Value("${kafka.topic.response}")
    private String responseTopic;

    private final KafkaTemplate<String, String> kafkaTemplate;

    /**
     * Publishes a single message with an optional partitioning key.
     * Messages with the same key stay in order on one partition.
     *
     * @param value serialized payload
     * @param type message type header value
     * @param key optional partition key
     * @param traceId tracing identifier
     * @param topic target Kafka topic
     */
    public void publish(String value, TemplateMessageType type, String key, String traceId, String topic) {
        publish(value, type, key, traceId, topic, null);
    }

    public void publish(String value, TemplateMessageType type, String key, String traceId, String topic, Long eventId) {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        String userId = null;
        String roles = null; // not collection, cuz only strings can be provided in headers

        log.debug("Publishing Kafka message with traceId={}", traceId);

        if (auth instanceof UsernamePasswordAuthenticationToken upAuth) {
            userId = (String) upAuth.getPrincipal();

            roles = upAuth.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(","));
        }

        kafkaTemplate.send(
                formMessage(
                        value,
                        type,
                        topic,
                        key,
                        traceId,
                        userId,
                        roles,
                        null,
                        eventId
                )
        );
    }

    private Message<String> formMessage(
            String payload,
            TemplateMessageType type,
            String topic,
            String key,
            String traceId,
            String userId,
            String roles,
            String correlationId,
            Long eventId
    ) {
        MessageBuilder<String> builder = MessageBuilder
                .withPayload(payload)
                .setHeader(KafkaHeaders.TOPIC, topic);

        if (key != null && !key.isBlank())
            builder.setHeader(KafkaHeaders.KEY, key);

        if (traceId != null && !traceId.isBlank())
            builder.setHeader("X-Trace-Id", traceId);

        if (userId != null && !userId.isBlank())
            builder.setHeader("X-User-Id", userId);

        if (roles != null && !roles.isBlank())
            builder.setHeader("X-Roles", roles);

        if (correlationId != null && !correlationId.isBlank())
            builder.setHeader(KafkaHeaders.CORRELATION_ID, correlationId);

        if (eventId != null)
            builder.setHeader("X-Event-Id", eventId.toString());

        builder.setHeader("X-Message-Type", Objects.requireNonNullElse(type, TemplateMessageType.UNDEFINED));

        return builder.build();
    }

    /**
     * Publishes a single message and generates trace id if missing.
     *
     * @param value serialized payload
     * @param type message type header value
     * @param traceId tracing identifier
     * @param topic target Kafka topic
     */
    public void publish(String value, TemplateMessageType type, String traceId, String topic) {
        publish(value, type, null,
                traceId == null ? UUID.randomUUID().toString() : traceId,
                topic
        );
    }

    public void publish(String value, TemplateMessageType type, String traceId, String topic, Long eventId) {
        publish(value, type, null,
                traceId == null ? UUID.randomUUID().toString() : traceId,
                topic,
                eventId
        );
    }

    /**
     * Publishes a batch of messages with a shared correlation id.
     *
     * @param values serialized payloads
     * @param type message type header value
     * @param topic target Kafka topic
     * @param correlationId correlation id to propagate
     */
    public void publish(List<String> values, TemplateMessageType type, String topic, String correlationId) {
        for (String value : values) {
            kafkaTemplate.send(
                    formMessage(
                            value,
                            type,
                            topic,
                            null,
                            null,
                            null,
                            null,
                            correlationId,
                            null
                    )
            );
        }
    }

    /**
     * Sends a reply message to the configured response topic.
     *
     * @param value serialized response payload
     * @param type message type header value
     * @param correlationId correlation id for request-reply
     * @param traceId tracing identifier
     */
    public void reply(String value, TemplateMessageType type, String correlationId, String traceId) {
        kafkaTemplate.send(
                formMessage(
                        value,
                        type,
                        responseTopic,
                        null,
                        traceId,
                        null,
                        null,
                        correlationId,
                        null
                )
        );
    }
}
