package com.grind.core.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grind.core.dto.entity.TaskDTO;
import com.grind.core.dto.entity.TrackDTO;
import com.grind.core.dto.wrap.Reply;
import com.grind.core.enums.CoreMessageType;
import com.grind.core.enums.coreMessageTypes.CoreTaskReqMsgType;
import com.grind.core.enums.coreMessageTypes.CoreTrackReqMsgType;
import com.grind.core.service.handler.TrackReplyHandler;
import lombok.RequiredArgsConstructor;
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
    private final KafkaProducer kafkaProducer;
    private final TrackReplyHandler replyHandler;
    private final ObjectMapper objectMapper;
    private final static List<CoreTrackReqMsgType> TO_PUBLISH_EVENT = List.of(
            CoreTrackReqMsgType.CHANGE_TRACK,
            CoreTrackReqMsgType.CREATE_TRACK,
            CoreTrackReqMsgType.DELETE_TRACK
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
            CoreTrackReqMsgType type = CoreTrackReqMsgType.valueOf(messageType);
            Reply<?> rep = replyHandler.routeReply(type, payload);
            if (TO_PUBLISH_EVENT.contains(type) && rep.body().status() == HttpStatus.OK) {
//                TODO publish events for all tasks, which are connected with sprints of this track
                Object repPayload = rep.body().payload();

                if (repPayload instanceof TrackDTO dto) {
                    // someService.findTasksWhichChangedAndGenEvents()
                    return;
                }
//                kafkaProducer.publish(
//                        rep.body(),
//                        rep.type(),
//                        traceId,
//                        coreEvTrackTopic
//                );
            }
            String replyPayload = objectMapper.writeValueAsString(rep.body());
            kafkaProducer.reply(replyPayload, rep.type(), correlationId, traceId);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }


}
