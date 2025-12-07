package com.grind.gateway.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final PendingRegistry pendingRegistry;

    @Value("${app.kafka.response-timeout-ms:5000}")
    private long responseTimeoutMs;

    /**
     * Publishes single with key message. <br>
     * Messages with one key will be published to the same partition. <br>
     * That means messages will be published and consumed in turn (from first to last)
     *
     * @param value
     */
    public void publish(
            Object value,
            String key,
            String topic,
            String correlationId
    ) {
        pendingRegistry.put(correlationId, new CompletableFuture<>());
        var auth = SecurityContextHolder.getContext().getAuthentication();

        String userId = null;
        String roles = null; // not collection, cuz only strings can be provided in headers
        String jsonValue;

        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            userId = jwtAuth.getToken().getSubject();
            roles = jwtAuth
                    .getAuthorities()
                    .stream().map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(","));
        }

        try {
            jsonValue = objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            jsonValue = value.toString();
        }

        MessageBuilder<String> builder = MessageBuilder
                .withPayload(jsonValue)
                .setHeader(KafkaHeaders.TOPIC, topic);

        if (key!= null && !key.isBlank())
            builder.setHeader(KafkaHeaders.KEY, key);

        builder.setHeader("X-Trace-Id", UUID.randomUUID().toString());

        builder.setHeader(KafkaHeaders.CORRELATION_ID, correlationId);

        if (userId!=null && !userId.isBlank())
            builder.setHeader("X-User-Id", userId);

        if (roles!=null && !roles.isBlank())
            builder.setHeader("X-Roles", roles);

        Message<String> message = builder.build();

        kafkaTemplate.send(message);
    }

    /**
     * Publishes a group of messages
     *
     * @param values
     */
    public void publish(List<Object> values, String topic, String correlationId) {
        for (Object value : values) {
            publish(value, topic, correlationId);
        }
    }

    /**
     * Publishes single message
     *
     * @param value
     */
    public void publish(Object value, String topic, String correlationId) {
        publish(value, null, topic, correlationId);
    }

    /**
     * Publishes a group of messages to the same partition <br>
     * That means messages will be published and consumed in turn (from first to last)
     *
     * @param values
     */
    public void publishOrdered(List<Object> values, String traceId, String correlationId) {
        String key = UUID.randomUUID().toString();
        for (Object value : values) {
            publish(value, key, traceId, correlationId);
        }
    }

    public Object retrieveResponse(String correlationId) throws TimeoutException {
        CompletableFuture<Object> response = pendingRegistry.get(correlationId);
        if (response == null){
            throw new IllegalStateException("No pending request with correlationId:"+correlationId);
        }
        try {
            return response.get(responseTimeoutMs, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            pendingRegistry.remove(correlationId);
            throw e;
        } catch (Exception e) {
            pendingRegistry.remove(correlationId);
            throw new RuntimeException("Ошибка при ожидании ответа из Kafka", e);
        }
    }
}
