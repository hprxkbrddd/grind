package com.grind.statistics.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
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

@Service
@RequiredArgsConstructor
public class KafkaProducer {
    @Value("${kafka.topic.response}")
    private String responseTopic;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Publishes single with key message. <br>
     * Messages with one key will be published to the same partition. <br>
     * That means messages will be published and consumed in turn (from first to last)
     *
     * @param value
     */
    public void publish(Object value, String key, String traceId, String topic) {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        String userId = null;
        String roles = null; // not collection, cuz only strings can be provided in headers
        String jsonValue;

        System.out.println(">>>>> Trace id: " + traceId);

        if (auth instanceof UsernamePasswordAuthenticationToken upAuth) {
            userId = (String) upAuth.getPrincipal();

            roles = upAuth.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(","));
        }

        try {
            jsonValue = objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            jsonValue = value.toString();
        }

        kafkaTemplate.send(
                formMessage(
                        jsonValue,
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
     * Publishes a group of messages
     *
     * @param values
     */
    public void publish(List<Object> values, String traceId, String topic) {
        String trId = traceId == null ? UUID.randomUUID().toString() : traceId;

        for (Object value : values) {
            publish(value, trId, topic);
        }
    }

    /**
     * Publishes single message
     *
     * @param value
     */
    public void publish(Object value, String traceId, String topic) {
        publish(value, null,
                traceId == null ? UUID.randomUUID().toString() : traceId,
                topic
        );
    }

    /**
     * Publishes a group of messages to the same partition <br>
     * That means messages will be published and consumed in turn (from first to last)
     *
     * @param values
     */
    public void publishOrdered(List<Object> values, String traceId, String topic) {
        String key = UUID.randomUUID().toString();
        String trId = traceId == null ? UUID.randomUUID().toString() : traceId;
        for (Object value : values) {
            publish(value, key, trId);
        }
    }

    public void reply(Object value, String correlationId, String traceId) {
        String jsonValue;

        try {
            jsonValue = objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            jsonValue = value.toString();
        }

        kafkaTemplate.send(
                formMessage(
                        jsonValue,
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