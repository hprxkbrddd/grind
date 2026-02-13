package com.grind.gateway.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grind.gateway.dto.Body;
import com.grind.gateway.enums.CoreMessageType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final PendingRegistry pendingRegistry;
    private final ObjectMapper objectMapper;


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
            CoreMessageType type,
            String key,
            String topic,
            String correlationId
    ) {
        pendingRegistry.put(correlationId, new CompletableFuture<>());
        var auth = SecurityContextHolder.getContext().getAuthentication();

        String userId = null;
        String roles = null; // not collection, cuz only strings can be provided in headers

        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            userId = jwtAuth.getToken().getSubject();
            roles = jwtAuth
                    .getAuthorities()
                    .stream().map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(","));
        }

        if (value == null) value = "";
        MessageBuilder<Object> builder = MessageBuilder
                .withPayload(value)
                .setHeader(KafkaHeaders.TOPIC, topic);

        if (key != null && !key.isBlank())
            builder.setHeader(KafkaHeaders.KEY, key);

        builder.setHeader("X-Trace-Id", UUID.randomUUID().toString());

        builder.setHeader(KafkaHeaders.CORRELATION_ID, correlationId);

        if (userId != null && !userId.isBlank())
            builder.setHeader("X-User-Id", userId);

        if (roles != null && !roles.isBlank())
            builder.setHeader("X-Roles", roles);

        builder.setHeader("X-Message-Type", Objects.requireNonNullElse(type, CoreMessageType.UNDEFINED).toString());

        Message<Object> message = builder.build();

        kafkaTemplate.send(message);
    }

    /**
     * Publishes single message
     *
     * @param value
     */
    public void publish(Object value, CoreMessageType type, String topic, String correlationId) {
        publish(value, type, null, topic, correlationId);
    }

    public void publishBodiless(CoreMessageType type, String topic, String correlationId) {
        publish(null, type, null, topic, correlationId);
    }

    public Body retrieveResponse(String correlationId) {
        CompletableFuture<Body> future = pendingRegistry.get(correlationId);
        if (future == null) {
            throw new IllegalStateException("No pending request with correlationId: " + correlationId);
        }

        try {
            return future.get(responseTimeoutMs, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            future.cancel(false);
            return Body.of("Gateway timeout exceeded", HttpStatus.GATEWAY_TIMEOUT);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Body.of("Interrupted while waiting for response", HttpStatus.INTERNAL_SERVER_ERROR);

        } catch (ExecutionException e) {
            // причина из бизнес-логики/обработчика ответа
            Throwable cause = e.getCause();
            return Body.of("Could not handle Kafka response: " + (cause != null ? cause.getMessage() : e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            pendingRegistry.remove(correlationId);
        }
    }

    public Body requestReply(Object body, CoreMessageType type, String topic) {
        String correlationId = UUID.randomUUID().toString();
        try {
            publish(
                    objectMapper.writeValueAsString(body),
                    type,
                    topic,
                    correlationId
            );
        } catch (JsonProcessingException e) {
            return Body.of("Request serialization exception", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return retrieveResponse(correlationId);
    }
}
