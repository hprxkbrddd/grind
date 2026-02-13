package com.grind.core.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grind.core.enums.CoreMessageType;
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

@Service
@RequiredArgsConstructor
public class KafkaProducer {
    private static final Logger log = LoggerFactory.getLogger(KafkaProducer.class);
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
    public void publish(Object value, CoreMessageType type, String key, String traceId, String topic) {
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
                        type,
                        topic,
                        key,
                        traceId,
                        userId,
                        roles,
                        null
                )
        );
    }

    private Message<Object> formMessage(
            Object payload,
            CoreMessageType type,
            String topic,
            String key,
            String traceId,
            String userId,
            String roles,
            String correlationId
    ) {
        MessageBuilder<Object> builder = MessageBuilder
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

        builder.setHeader("X-Event-Id", UUID.randomUUID().toString());

        builder.setHeader("X-Message-Type", Objects.requireNonNullElse(type, CoreMessageType.UNDEFINED));

        return builder.build();
    }

    /**
     * Publishes single message
     *
     * @param value
     */
    public void publish(Object value, CoreMessageType type, String traceId, String topic) {
        publish(value, type, null,
                traceId == null ? UUID.randomUUID().toString() : traceId,
                topic
        );
    }

    public void reply(Object value, CoreMessageType type, String correlationId, String traceId) {
        kafkaTemplate.send(
                formMessage(
                        value,
                        type,
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
