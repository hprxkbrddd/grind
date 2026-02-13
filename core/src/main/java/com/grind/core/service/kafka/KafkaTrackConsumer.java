package com.grind.core.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grind.core.dto.wrap.Reply;
import com.grind.core.enums.CoreMessageType;
import com.grind.core.service.application.TrackService;
import com.grind.core.service.handler.TrackReplyHandler;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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
    private static final Logger log = LoggerFactory.getLogger(KafkaTrackConsumer.class);
    private final KafkaProducer kafkaProducer;
    private final TrackReplyHandler replyHandler;
    private final ObjectMapper objectMapper;
    private final TrackService service;
    private final static List<CoreMessageType> TO_PUBLISH_EVENT = List.of(
            CoreMessageType.CHANGE_TASK,
            CoreMessageType.CREATE_TASK,
            CoreMessageType.PLAN_TASK_DATE,
            CoreMessageType.PLAN_TASK_SPRINT,
            CoreMessageType.COMPLETE_TASK
    );

    @Value("${kafka.topic.core.event.track}")
    private String coreEvTrackTopic;

    @KafkaListener(id = "core-server-track", topics = "core.request.track")
    public void listen(
            @Payload String payload,
            @Header(KafkaHeaders.CORRELATION_ID) String correlationId,
            @Header("X-Trace-Id") String traceId,
            @Header("X-User-Id") String userId,
            @Header(value = "X-Roles", required = false) String roles,
            @Header(value = "X-Message-Type") String messageType
    ) {
        try {
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


            // HANDLING REQUEST
            CoreMessageType type = CoreMessageType.valueOf(messageType);
            Reply rep = routeReply(type, payload);
            if (TO_PUBLISH_EVENT.contains(type)) {
                kafkaProducer.publish(
                        rep.body(),
                        rep.type(),
                        traceId,
                        coreEvTrackTopic
                );
            }
            String replyPayload = objectMapper.writeValueAsString(rep.body());
            kafkaProducer.reply(replyPayload, rep.type(), correlationId, traceId);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    private Reply routeReply(CoreMessageType type, String payload) {
        switch (type) {
            case GET_TRACKS_OF_USER -> {
                return replyHandler.handleGetTracksOfUser();
            }
            case GET_TRACK -> {
                return replyHandler.handleGetTrack(payload);
            }
            case GET_ALL_TRACKS -> {
                return replyHandler.handleGetAllTracks();
            }
            case GET_SPRINTS_OF_TRACK -> {
                return replyHandler.handleGetSprintsOfTrack(payload);
            }
            case CHANGE_TRACK -> {
                return replyHandler.handleChangeTrack(payload);
            }
            case CREATE_TRACK -> {
                return replyHandler.handleCreateTrack(payload);
            }
            case DELETE_TRACK -> {
                return replyHandler.handleDeleteTrack(payload);
            }
            case UNDEFINED -> {
                return Reply.error(
                        new IllegalStateException("Unhandled message type"),
                        HttpStatus.INTERNAL_SERVER_ERROR
                );
            }
            default -> throw new UnsupportedOperationException("Message type is not related to tracks");
        }
    }
}
