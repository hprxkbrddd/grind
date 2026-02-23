package com.grind.statistics.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grind.statistics.enums.StatisticsMessageType;
import com.grind.statistics.dto.wrap.Reply;
import com.grind.statistics.service.handler.SprintStatisticsHandler;
import com.grind.statistics.service.handler.TrackStatisticsHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.grind.statistics.enums.StatisticsMessageType.*;
import static com.grind.statistics.util.ConsumerHelper.authenticate;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaGatewayConsumer {

    private final TrackStatisticsHandler trackStatisticsHandler;
    private final SprintStatisticsHandler sprintStatisticsHandler;
    private final KafkaProducer kafkaProducer;
    private final ObjectMapper objectMapper;
    private static final List<StatisticsMessageType> trackStats = List.of(
            GET_TRACK_COMPLETION,
            GET_TRACK_REMAINING_LOAD,
            GET_TRACK_OVERDUE_PRESSURE,
            GET_TRACK_ACTIVE_TASKS_AGING,
            GET_TRACK_WORK_IN_PROGRESS,
            GET_TRACK_OVERDUE_AMONG_COMPLETED,
            GET_COMPLETED_LAST_MONTH,
            GET_COMPLETED_LAST_WEEK
    );
    private static final List<StatisticsMessageType> sprintStats = List.of(
            GET_SPRINT_COMPLETION,
            GET_SPRINT_REMAINING_LOAD,
            GET_SPRINT_OVERDUE_PRESSURE,
            GET_SPRINT_ACTIVE_TASKS_AGING,
            GET_SPRINT_WORK_IN_PROGRESS,
            GET_SPRINT_OVERDUE_AMONG_COMPLETED
    );

    @KafkaListener(id = "stats-server", topics = "statistics.request", containerFactory = "kafkaSingleListenerContainerFactory")
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
            authenticate(userId, roles);

            // HANDLING REQUEST
            StatisticsMessageType type = StatisticsMessageType.valueOf(messageType);

            Reply<?> rep;

            if (trackStats.contains(type))
                rep = trackStatisticsHandler.routeReply(type, payload);
            else if (sprintStats.contains(type))
                rep = sprintStatisticsHandler.routeReply(type, payload);
            else throw new UnsupportedOperationException("Message type is not instance of 'StatisticsMessageType'");

            String replyPayload = objectMapper.writeValueAsString(rep.body());

            kafkaProducer.reply(replyPayload, correlationId, traceId);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}
