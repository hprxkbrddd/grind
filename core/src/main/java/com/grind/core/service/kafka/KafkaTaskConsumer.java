package com.grind.core.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grind.core.dto.entity.TaskDTO;
import com.grind.core.dto.wrap.Reply;
import com.grind.core.enums.coreMessageTypes.CoreTaskReqMsgType;
import com.grind.core.enums.coreMessageTypes.SystemMsgType;
import com.grind.core.service.handler.TaskReplyHandler;
import lombok.RequiredArgsConstructor;
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
public class KafkaTaskConsumer {

    private final KafkaProducer kafkaProducer;
    private final TaskReplyHandler replyHandler;
    private final ObjectMapper objectMapper;
    private final OutboxService outboxService;
    private final static List<CoreTaskReqMsgType> TO_PUBLISH_EVENT = List.of(
            CoreTaskReqMsgType.CREATE_TASK,
            CoreTaskReqMsgType.PLAN_TASK_DATE,
            CoreTaskReqMsgType.PLAN_TASK_SPRINT,
            CoreTaskReqMsgType.COMPLETE_TASK,
            CoreTaskReqMsgType.MOVE_TASK_TO_BACKLOG,
            CoreTaskReqMsgType.DELETE_TASK
    );

    @KafkaListener(id = "core-server-task", topics = "core.request.task")
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
            CoreTaskReqMsgType type = CoreTaskReqMsgType.valueOf(messageType);
            Reply<?> rep = replyHandler.routeReply(type, payload);
            if (TO_PUBLISH_EVENT.contains(type) && rep.body().status() == HttpStatus.OK) {
                Object repPayload = rep.body().payload();

                if (repPayload instanceof TaskDTO dto) {
                    outboxService.genEvent(dto, rep.type(), traceId);
                } else if (repPayload instanceof List<?> list) {
                    List<TaskDTO> dtoList = list.stream()
                            .filter(TaskDTO.class::isInstance)
                            .map(TaskDTO.class::cast)
                            .toList();

                    if (!dtoList.isEmpty()) {
                        outboxService.genEvents(dtoList, rep.type(), traceId);
                    }
                }
            }
            String replyPayload = rep.type().code().equals(SystemMsgType.ERROR.name()) ?
                    rep.body().error() : objectMapper.writeValueAsString(rep.body());

            kafkaProducer.reply(replyPayload, rep.type(), correlationId, traceId);


        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}
