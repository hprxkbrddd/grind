package com.grind.template.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grind.template.dto.wrap.Reply;
import com.grind.template.enums.TemplateMessageType;
import com.grind.template.service.handler.TemplateReplyHandler;
import com.grind.template.util.TraceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaHandler;
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
public class KafkaGatewayConsumer {

    private final TemplateReplyHandler replyHandler;
    private final ObjectMapper objectMapper;
    private final KafkaProducer kafkaProducer;

    /**
     * Handles task requests and sends replies to the gateway.
     *
     * @param payload serialized request body
     * @param correlationId request-reply correlation id
     * @param traceId tracing identifier
     * @param userId authenticated user id header
     * @param roles optional roles header
     * @param messageType message type header
     */
    @KafkaListener(id = "template-server", topics = "template.request")
    public void listen(
            @Payload String payload,
            @Header(KafkaHeaders.CORRELATION_ID) String correlationId,
            @Header("X-Trace-Id") String traceId,
            @Header("X-User-Id") String userId,
            @Header(value = "X-Roles", required = false) String roles,
            @Header(value = "X-Message-Type") String messageType
    ){
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
            TraceContext.setTraceId(traceId);

            // HANDLING REQUEST
            TemplateMessageType type = TemplateMessageType.valueOf(messageType);
            Reply<?> rep = replyHandler.routeReply(type, payload);
            String replyPayload = objectMapper.writeValueAsString(rep.body());

            kafkaProducer.reply(replyPayload, rep.type(), correlationId, traceId);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } finally {
            SecurityContextHolder.clearContext();
            TraceContext.clear();
        }
    }
}
