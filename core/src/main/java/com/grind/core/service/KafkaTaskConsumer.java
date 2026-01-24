package com.grind.core.service;

import com.grind.core.dto.CoreMessageType;
import com.grind.core.dto.Reply;
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
public class KafkaTaskConsumer {

    private final KafkaProducer kafkaProducer;
    private final TaskReplyHandler replyHandler;
    private final static List<CoreMessageType> TO_PUBLISH_EVENT = List.of(
            CoreMessageType.CHANGE_TASK,
            CoreMessageType.CREATE_TASK,
            CoreMessageType.PLAN_TASK_DATE,
            CoreMessageType.PLAN_TASK_SPRINT,
            CoreMessageType.COMPLETE_TASK
    );

    @Value("${kafka.topic.core.event.task}")
    private String coreEvTaskTopic;

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
            CoreMessageType type = CoreMessageType.valueOf(messageType);
            Reply rep = routeReply(type, payload);
            if (TO_PUBLISH_EVENT.contains(type)) {
                kafkaProducer.publish(
                        rep.payload(),
                        rep.type(),
                        traceId,
                        coreEvTaskTopic
                );
            }
            kafkaProducer.reply(rep.payload(), rep.type(), correlationId, traceId);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    private Reply routeReply(CoreMessageType type, String payload) {
        switch (type) {
            case GET_TASKS_OF_TRACK -> {
                return replyHandler.handleGetTasksOfTrack(payload);
            }
            case GET_TASKS_OF_SPRINT -> {
                return replyHandler.handleGetTasksOfSprint(payload);
            }
            case GET_TASK -> {
                return replyHandler.handleGetTask(payload);
            }
            case GET_ALL_TASKS -> {
                return replyHandler.handleGetAllTasks();
            }
            case CHANGE_TASK -> {
                return replyHandler.handleChangeTask(payload);
            }
            case PLAN_TASK_SPRINT -> {
                return replyHandler.handlePlanTaskSprint(payload);
            }
            case PLAN_TASK_DATE -> {
                return replyHandler.handlePlanTaskDate(payload);
            }
            case COMPLETE_TASK -> {
                return replyHandler.handleCompleteTask(payload);
            }
            case CREATE_TASK -> {
                return replyHandler.handleCreateTask(payload);
            }
            case DELETE_TASK -> {
                return replyHandler.handleDeleteTask(payload);
            }
            case UNDEFINED -> {
                return Reply.error(
                        new IllegalStateException("Unhandled message type"),
                        HttpStatus.INTERNAL_SERVER_ERROR
                );
            }
            default -> throw new UnsupportedOperationException("САК МА ДИК ПУСИ ФАК");
        }
    }
}
