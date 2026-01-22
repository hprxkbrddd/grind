package com.grind.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grind.core.dto.CoreMessageDTO;
import com.grind.core.dto.CoreMessageType;
import com.grind.core.request.Track.ChangeTrackRequest;
import com.grind.core.request.Track.CreateTrackRequest;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
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
    private final TrackService service;

    @Value("${kafka.topic.core.event.track}")
    private String coreEvTrackTopic;

    @KafkaListener(id = "core-server-track", topics = "core.request.track")
    public void listen(
            @Payload String payload,
            @Header(KafkaHeaders.CORRELATION_ID) String correlationId,
            @Header("X-Trace-Id") String traceId,
            @Header("X-User-Id") String userId,
            @Header(value = "X-Roles", required = false) String roles,
            @Header(value = "X-Message-type") String messageType
    ) throws JsonProcessingException, InterruptedException, BadRequestException {
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
        switch (type) {
            case GET_TRACKS_OF_USER -> kafkaProducer.reply(
                    service.getByUserId(userId),
                    CoreMessageType.TRACKS_OF_USER,
                    correlationId,
                    traceId
            );
            case GET_TRACK -> kafkaProducer.reply(
                    service.getById(payload),
                    CoreMessageType.TRACK,
                    correlationId,
                    traceId
            );

            case CHANGE_TRACK -> {
                ChangeTrackRequest req = objectMapper.readValue(payload, ChangeTrackRequest.class);
                kafkaProducer.publish(
                        service.changeTrack(
                                req.id(),
                                req.name(),
                                req.description(),
                                req.petId(),
                                req.startDate(),
                                req.targetDate(),
                                req.sprintLength(),
                                req.messagePolicy(),
                                req.status()
                        ),
                        CoreMessageType.TRACK_CHANGED,
                        traceId,
                        coreEvTrackTopic
                );
            }
            case CREATE_TRACK -> {
                CreateTrackRequest req = objectMapper.readValue(payload, CreateTrackRequest.class);
                kafkaProducer.publish(
                        service.createTrack(
                                req.name(),
                                req.description(),
                                req.petId(),
                                req.sprintLength(),
                                req.startDate(),
                                req.targetDate(),
                                req.messagePolicy(),
                                req.status()
                        ),
                        CoreMessageType.TRACK_CREATED,
                        traceId,
                        coreEvTrackTopic);
            }
            case DELETE_TRACK -> kafkaProducer.publish(
                    service.deleteTrack(payload),
                    CoreMessageType.TRACK_DELETED,
                    traceId,
                    coreEvTrackTopic
            );
            case UNDEFINED -> kafkaProducer.publishBodiless(CoreMessageType.UNDEFINED, traceId, coreEvTrackTopic);
            default -> throw new BadRequestException("Not request type");
        }
    }
}
