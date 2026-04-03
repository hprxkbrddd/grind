package com.grind.gateway.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grind.gateway.dto.Body;
import com.grind.gateway.enums.CoreMessageType;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

/**
 * Publishes gateway Kafka requests and handles request-reply workflows.
 * Maintains pending futures for correlation-based responses.
 */
@Service
@RequiredArgsConstructor
public class KafkaProducer {
    private static final Logger log = LoggerFactory.getLogger(KafkaProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final PendingRegistry pendingRegistry;
    private final ObjectMapper objectMapper;


    @Value("${app.kafka.response-timeout-ms:5000}")
    private long responseTimeoutMs;

    /**
     * Publishes a single message with an optional partitioning key.
     * Messages with the same key stay in order on one partition.
     *
     * @param value serialized payload
     * @param type message type header value
     * @param key optional partition key
     * @param topic target Kafka topic
     * @param correlationId request-reply correlation id
     */
    public void publish(
            String value,
            String type,
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

        String traceId = UUID.randomUUID().toString();
        log.debug("Publishing Kafka message with traceId={}", traceId);

        kafkaTemplate.send(
                formMessage(
                        value == null ? "" : value,
                        type,
                        topic,
                        key,
                        traceId,
                        userId,
                        roles,
                        correlationId
                )
        );
    }

    private Message<String> formMessage(
            String payload,
            String type,
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

        if (correlationId != null && !correlationId.isBlank())
            builder.setHeader(KafkaHeaders.CORRELATION_ID, correlationId);

        if (userId != null && !userId.isBlank())
            builder.setHeader("X-User-Id", userId);

        if (roles != null && !roles.isBlank())
            builder.setHeader("X-Roles", roles);

        builder.setHeader("X-Message-Type", Objects.requireNonNullElse(type, CoreMessageType.UNDEFINED).toString());

        return builder.build();
    }

    /**
     * Publishes a message without an explicit partition key.
     *
     * @param value serialized payload
     * @param type message type header value
     * @param topic target Kafka topic
     * @param correlationId request-reply correlation id
     */
    public void publish(String value, String type, String topic, String correlationId) {
        publish(value, type, null, topic, correlationId);
    }

    /**
     * Publishes a message with an empty payload.
     *
     * @param type message type header value
     * @param topic target Kafka topic
     * @param correlationId request-reply correlation id
     */
    public void publishBodiless(String type, String topic, String correlationId) {
        publish("", type, null, topic, correlationId);
    }

    /**
     * Waits for a response or timeout for the given correlation id.
     *
     * @param correlationId request-reply correlation id
     * @return response body
     */
    public Body<?> retrieveResponse(String correlationId) {
        CompletableFuture<Body<?>> future = pendingRegistry.get(correlationId);
        if (future == null) {
            throw new IllegalStateException("No pending request with correlationId: " + correlationId);
        }

        try {
            return future.get(responseTimeoutMs, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            future.cancel(false);
            return Body.err("Gateway timeout exceeded", HttpStatus.GATEWAY_TIMEOUT);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Body.err("Interrupted while waiting for response", HttpStatus.INTERNAL_SERVER_ERROR);

        } catch (ExecutionException e) {
            // причина из бизнес-логики/обработчика ответа
            Throwable cause = e.getCause();
            return Body.err("Could not handle Kafka response: " + (cause != null ? cause.getMessage() : e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            pendingRegistry.remove(correlationId);
        }
    }

    /**
     * Sends a request and waits for a response.
     *
     * @param body request payload
     * @param type message type header value
     * @param topic target Kafka topic
     * @return response body
     */
    public Body<?> requestReply(Object body, String type, String topic) {
        String correlationId = UUID.randomUUID().toString();
        try {
            publish(
                    objectMapper.writeValueAsString(body),
                    type,
                    topic,
                    correlationId
            );
        } catch (JsonProcessingException e) {
            return Body.err("Request serialization exception", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return retrieveResponse(correlationId);
    }
}
