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
public class KafkaTrackConsumer {
    private final KafkaProducer kafkaProducer;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topic.core.event.track}")
    private String coreEvTrackTopic;

    @KafkaListener(id = "core-server-track", topics = "core.request.track")
    public void listen(
            @Payload String payload,
            @Header(KafkaHeaders.CORRELATION_ID) String correlationId,
            @Header("X-Trace-Id") String traceId,
            @Header("X-User-Id") String userId,
            @Header(value = "X-Roles", required = false) String roles
    ) throws JsonProcessingException, InterruptedException {
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
            case GET_TRACKS_OF_USER -> {
                String result;
                // ---CODE TO REPLACE---
                System.out.println("fetching all TRACKS of USER:id" +
                        SecurityContextHolder.getContext().getAuthentication().getName()  // the way to access userId and roles
                        + "..."
                );
                System.out.println(">>> PAYLOAD\n" + objectMapper.writeValueAsString(msg));
                Thread.sleep(500L);
                result = "some json as string, or exception";
                // ---CODE TO REPLACE END---
                response = new CoreMessageDTO(
                        CoreMessageType.TASKS_OF_TRACK,
                        result
                );
                kafkaProducer.reply(response, correlationId, traceId);
            }
            case GET_TRACK -> {
                String result;
                // ---CODE TO REPLACE---
                System.out.println("fetching TRACK...");
                System.out.println(">>> PAYLOAD\n" + objectMapper.writeValueAsString(msg));
                Thread.sleep(500L);
                result = "some json as string, or exception";
                // ---CODE TO REPLACE END---
                response = new CoreMessageDTO(
                        CoreMessageType.TASKS_OF_TRACK,
                        result
                );
                kafkaProducer.reply(response, correlationId, traceId);
            }
            case CHANGE_TRACK -> {
                String result; // if present
                // ---CODE TO REPLACE---
                System.out.println("mocking CHANGE track action");
                System.out.println(">>> PAYLOAD\n"+objectMapper.writeValueAsString(msg));
                Thread.sleep(500L);
                result = "some json as string, or empty";
                // ---CODE TO REPLACE END---
                response = new CoreMessageDTO(
                        CoreMessageType.TRACK_CHANGED,
                        result
                );
                kafkaProducer.publish(response, traceId, coreEvTrackTopic);
            }
            case CREATE_TRACK -> {
                String result; // if present
                // ---CODE TO REPLACE---
                System.out.println("mocking CREATE track action");
                System.out.println(">>> PAYLOAD\n"+objectMapper.writeValueAsString(msg));
                Thread.sleep(500L);
                result = "some json as string, or empty";
                // ---CODE TO REPLACE END---
                response = new CoreMessageDTO(
                        CoreMessageType.TRACK_CREATED,
                        result
                );
                kafkaProducer.publish(response, traceId, coreEvTrackTopic);
            }
            case DELETE_TRACK -> {
                String result; // if present
                // ---CODE TO REPLACE---
                System.out.println("mocking DELETE track action");
                System.out.println(">>> PAYLOAD\n"+objectMapper.writeValueAsString(msg));
                Thread.sleep(500L);
                result = "some json as string, or empty";
                // ---CODE TO REPLACE END---
                response = new CoreMessageDTO(
                        CoreMessageType.TRACK_DELETED,
                        result
                );
                kafkaProducer.publish(response, traceId, coreEvTrackTopic);
            }
            case UNDEFINED -> {
                CoreMessageDTO result;
                // ---CODE TO REPLACE---
                System.out.println("type is undefined. mocking processing");
                System.out.println(">>> PAYLOAD\n" + objectMapper.writeValueAsString(msg));
                Thread.sleep(500L);
                result = new CoreMessageDTO(
                        CoreMessageType.UNDEFINED,
                        "some json as string, or empty"
                );
                // ---CODE TO REPLACE END---
                kafkaProducer.publish(result, traceId, coreEvTrackTopic);
            }
            default -> {
                System.out.println("unknown type");
                kafkaProducer.publish("unknown type in core.request.track. corrId:" + correlationId, traceId, coreEvTrackTopic);
            }
        }
    }
}
