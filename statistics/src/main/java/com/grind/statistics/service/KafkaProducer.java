package com.grind.statistics.service;

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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KafkaProducer {

    @Value("${kafka.topic.statistics.response}")
    private String statRes;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Publishes single with key message. <br>
     * Messages with one key will be published to the same partition. <br>
     * That means messages will be published and consumed in turn (from first to last)
     *
     * @param value
     */
    public void publish(Object value, String key) {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        String userId = null;
        String roles = null; // not collection, cuz only strings can be provided in headers
        String traceId = null; // to identify request for all microservices
        String jsonValue;

        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (requestAttributes != null) {
            traceId = requestAttributes.getRequest().getHeader("X-Trace-Id");
        }

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
                .setHeader(KafkaHeaders.TOPIC, statRes);

        if (key!= null && !key.isBlank())
            builder.setHeader(KafkaHeaders.KEY, key);

        if (traceId != null && !traceId.isBlank())
            builder.setHeader("X-Trace-Id", traceId);
        else
            builder.setHeader("X-Trace-Id", UUID.randomUUID().toString());

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
    public void publish(List<Object> values) {
        for (Object value : values) {
            publish(value);
        }
    }

    /**
     * Publishes single message
     *
     * @param value
     */
    public void publish(Object value) {
        publish(value, null);
    }

    /**
     * Publishes a group of messages to the same partition <br>
     * That means messages will be published and consumed in turn (from first to last)
     *
     * @param values
     */
    public void publishOrdered(List<Object> values) {
        String key = UUID.randomUUID().toString();
        for (Object value : values) {
            publish(value, key);
        }
    }
}
