package com.grind.statistics.service.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

import static com.grind.statistics.util.ConsumerHelper.authenticate;
import static com.grind.statistics.util.ConsumerHelper.headerAsString;

public class KafkaGatewayConsumer {

    @KafkaListener(id = "stats-server", topics = "statistics.requestInsert")
    public void listen(
            @Payload String payload,
            @Header(KafkaHeaders.CORRELATION_ID) String correlationId,
            @Header("X-Trace-Id") String traceId,
            @Header("X-User-Id") String userId,
            @Header(value = "X-Roles", required = false) String roles
    ) {
        // FORMING AUTHENTICATION OBJECT
        authenticate(userId, roles);

        // CODE
        // ...
        // CODE
    }
}
