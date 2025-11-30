package com.grind.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grind.core.dto.CoreMessageDTO;
import com.grind.core.dto.CoreMessageType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.JsonParseException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KafkaTaskConsumer {

    private final KafkaProducer kafkaProducer;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topic.core.event.task}")
    private String coreEvTaskTopic;

    @KafkaListener(id = "core-server-task", topics = "core.request.task")
    public void listen(
            @Payload String payload,
            @Header(KafkaHeaders.CORRELATION_ID) String correlationId,
            @Header("X-Trace-Id") String traceId,
            @Header("X-User-Id") String userId,
            @Header(value = "X-Roles", required = false) String roles
    ) throws InterruptedException, JsonProcessingException {
        // FORMING AUTHENTICATION OBJECT
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

        // PARSING PAYLOAD
        CoreMessageDTO msg;
        try {
            msg = objectMapper.readValue(payload, CoreMessageDTO.class);
        } catch (JsonParseException e) {
            msg = new CoreMessageDTO(CoreMessageType.UNDEFINED, payload);
            System.out.println("could not parse. processing as string");
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // HANDLING REQUEST
        CoreMessageDTO response;
        switch (msg.type()) {
            case GET_TASKS_OF_TRACK -> {
                Object result;
                // ---CODE TO REPLACE---
                System.out.println("fetching all TASKS of TRACK...");
                System.out.println(">>> PAYLOAD\n" + objectMapper.writeValueAsString(msg));
                Thread.sleep(500L);
                result = "some object as dto, or exception";
                // ---CODE TO REPLACE END---
                response = new CoreMessageDTO(
                        CoreMessageType.TASKS_OF_TRACK,
                        result
                );
                kafkaProducer.reply(response, correlationId, traceId);
            }
            case GET_TASKS_OF_SPRINT -> {
                Object result;
                // ---CODE TO REPLACE---
                System.out.println("fetching all TASKS of SPRINT...");
                System.out.println(">>> PAYLOAD\n" + objectMapper.writeValueAsString(msg));
                Thread.sleep(500L);
                result = "some object as dto, or exception";
                // ---CODE TO REPLACE END---
                response = new CoreMessageDTO(
                        CoreMessageType.TASKS_OF_SPRINT,
                        result
                );
                kafkaProducer.reply(response, correlationId, traceId);
            }
            case GET_TASK -> {
                Object result;
                // ---CODE TO REPLACE---
                System.out.println("fetching TASK...");
                System.out.println(">>> PAYLOAD\n" + objectMapper.writeValueAsString(msg));
                Thread.sleep(500L);
                result = "some object as dto, or exception";
                // ---CODE TO REPLACE END---
                response = new CoreMessageDTO(
                        CoreMessageType.TASK,
                        result
                );
                kafkaProducer.reply(response, correlationId, traceId);
            }
            case CHANGE_TASK -> {
                Object result; // if present
                // ---CODE TO REPLACE---
                System.out.println("mocking CHANGE task action");
                System.out.println(">>> PAYLOAD\n" + objectMapper.writeValueAsString(msg));
                Thread.sleep(500L);
                result = "some object as dto, or empty";
                // ---CODE TO REPLACE END---
                response = new CoreMessageDTO(
                        CoreMessageType.TASK_CHANGED,
                        result
                );
                kafkaProducer.publish(response, traceId, coreEvTaskTopic);
            }
            case CREATE_TASK -> {
                Object result; // if present
                // ---CODE TO REPLACE---
                System.out.println("mocking CREATE task action");
                System.out.println(">>> PAYLOAD\n" + objectMapper.writeValueAsString(msg));
                Thread.sleep(500L);
                result = "some object as dto, or empty";
                // ---CODE TO REPLACE END---
                response = new CoreMessageDTO(
                        CoreMessageType.TASK_CREATED,
                        result
                );
                kafkaProducer.publish(response, traceId, coreEvTaskTopic);
            }
            case DELETE_TASK -> {
                Object result; // if present
                // ---CODE TO REPLACE---
                System.out.println("mocking DELETE task action");
                System.out.println(">>> PAYLOAD\n" + objectMapper.writeValueAsString(msg));
                Thread.sleep(500L);
                result = "some object as dto, or empty";
                // ---CODE TO REPLACE END---
                response = new CoreMessageDTO(
                        CoreMessageType.TASK_DELETED,
                        result
                );
                kafkaProducer.publish(response, traceId, coreEvTaskTopic);
            }
            case UNDEFINED -> {
                CoreMessageDTO result;
                // ---CODE TO REPLACE---
                System.out.println("type is undefined. mocking processing");
                System.out.println(">>> PAYLOAD\n" + objectMapper.writeValueAsString(msg));
                Thread.sleep(500L);
                result = new CoreMessageDTO(
                        CoreMessageType.UNDEFINED,
                        "some object as dto, or empty"
                );
                // ---CODE TO REPLACE END---
                kafkaProducer.publish(result, traceId, coreEvTaskTopic);
            }
            default -> {
                System.out.println("unknown type");
                kafkaProducer.publish("unknown type in core.request.task. corrId:" + correlationId, traceId, coreEvTaskTopic);
            }
        }
    }
}
