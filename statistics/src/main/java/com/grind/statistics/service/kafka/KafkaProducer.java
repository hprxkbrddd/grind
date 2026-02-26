package com.grind.statistics.service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
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
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Produces Kafka responses and events for the statistics service.
 * Adds trace, user, and role headers from the security context.
 */
@Service
@RequiredArgsConstructor
public class KafkaProducer {
    @Value("${kafka.topic.response}")
    private String responseTopic;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Publishes a single message with an optional partitioning key.
     * Messages with the same key stay in order on one partition.
     *
     * @param value serialized payload
     * @param key optional partition key
     * @param traceId tracing identifier
     * @param topic target Kafka topic
     */
    public void publish(String value, String key, String traceId, String topic) {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        String userId = null;
        String roles = null; // not collection, cuz only strings can be provided in headers

        System.out.println(">>>>> Trace id: " + traceId);

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
                        topic,
                        key,
                        traceId,
                        userId,
                        roles,
                        null
                )
        );
    }

    private Message<String> formMessage(
            String payload,
            String topic,
            String key,
            String traceId,
            String userId,
            String roles,
            String correlationId
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

        return builder.build();
    }

    /**
     * Publishes a batch of messages with a shared trace id.
     *
     * @param values serialized payloads
     * @param traceId tracing identifier
     * @param topic target Kafka topic
     */
    public void publish(List<String> values, String traceId, String topic) {
        String trId = traceId == null ? UUID.randomUUID().toString() : traceId;

        for (String value : values) {
            publish(value, trId, topic);
        }
    }

    /**
     * Publishes a single message and generates trace id if missing.
     *
     * @param value serialized payload
     * @param traceId tracing identifier
     * @param topic target Kafka topic
     */
    public void publish(String value, String traceId, String topic) {
        publish(value, null,
                traceId == null ? UUID.randomUUID().toString() : traceId,
                topic
        );
    }

    /**
     * Publishes a batch of messages with a shared partition key.
     * Messages stay in order on one partition.
     *
     * @param values serialized payloads
     * @param traceId tracing identifier
     * @param topic target Kafka topic
     */
    public void publishOrdered(List<String> values, String traceId, String topic) {
        String key = UUID.randomUUID().toString();
        String trId = traceId == null ? UUID.randomUUID().toString() : traceId;
        for (String value : values) {
            publish(value, key, trId);
        }
    }

    /**
     * Sends a reply message to the configured response topic.
     *
     * @param value serialized response payload
     * @param correlationId request-reply correlation id
     * @param traceId tracing identifier
     */
    public void reply(String value, String correlationId, String traceId) {
        kafkaTemplate.send(
                formMessage(
                        value,
                        responseTopic,
                        null,
                        traceId,
                        null,
                        null,
                        correlationId
                )
        );
    }
}
