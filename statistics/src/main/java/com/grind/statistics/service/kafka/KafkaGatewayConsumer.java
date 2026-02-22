package com.grind.statistics.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grind.statistics.enums.StatisticsMessageType;
import com.grind.statistics.dto.wrap.Reply;
import com.grind.statistics.service.handler.StatisticsHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import static com.grind.statistics.util.ConsumerHelper.authenticate;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaGatewayConsumer {

    private final StatisticsHandler replyHandler;
    private final KafkaProducer kafkaProducer;
    private final ObjectMapper objectMapper;

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

            Reply<?> rep = replyHandler.routeReply(type, payload);

            String replyPayload = objectMapper.writeValueAsString(rep.body());

            kafkaProducer.reply(replyPayload, correlationId, traceId);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}
