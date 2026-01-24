package com.grind.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grind.core.dto.CoreMessageType;
import com.grind.core.dto.PlanTaskDateDTO;
import com.grind.core.dto.PlanTaskSprintDTO;
import com.grind.core.model.Task;
import com.grind.core.request.Task.ChangeTaskRequest;
import com.grind.core.request.Task.CreateTaskRequest;
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
public class KafkaTaskConsumer {

    private final KafkaProducer kafkaProducer;
    private final ObjectMapper objectMapper;
    private final TaskService service;

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
    ) throws InterruptedException, JsonProcessingException, BadRequestException {
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
            case GET_TASKS_OF_TRACK -> kafkaProducer.reply(
                        service.getByTrack(payload).stream().map(Task::mapDTO),
                    CoreMessageType.TASKS_OF_TRACK,
                    correlationId,
                    traceId
            );
            case GET_TASKS_OF_SPRINT -> kafkaProducer.reply(
                    service.getBySprint(payload).stream().map(Task::mapDTO).toList(),
                    CoreMessageType.TASKS_OF_SPRINT,
                    correlationId,
                    traceId);

            case GET_TASK -> kafkaProducer.reply(
                    service.getById(payload),
                    CoreMessageType.TASK,
                    correlationId,
                    traceId
            );

            case GET_ALL_TASKS -> kafkaProducer.reply(
                    service.getAllTasks().stream().map(Task::mapDTO),
                    CoreMessageType.ALL_TASKS,
                    correlationId,
                    traceId
            );
            case CHANGE_TASK -> {
                ChangeTaskRequest req = objectMapper.readValue(payload, ChangeTaskRequest.class);
                kafkaProducer.publish(
                        service.changeTask(
                                req.taskId(),
                                req.title(),
                                req.description()
                        ),
                        CoreMessageType.TASK_CHANGED,
                        traceId,
                        coreEvTaskTopic
                );
            }
            case PLAN_TASK_SPRINT -> {
                PlanTaskSprintDTO req = objectMapper.readValue(payload, PlanTaskSprintDTO.class);
                kafkaProducer.publish(
                        service.planTaskSprint(
                                req.taskId(),
                                req.sprintId(),
                                req.dayOfSprint()
                        ),
                        CoreMessageType.TASK_PLANNED,
                        traceId,
                        coreEvTaskTopic
                );
            }
            case PLAN_TASK_DATE -> {
                PlanTaskDateDTO req = objectMapper.readValue(payload, PlanTaskDateDTO.class);
                kafkaProducer.publish(
                        service.planTaskByDate(
                                req.taskId(),
                                req.plannedDate()
                        ),
                        CoreMessageType.TASK_PLANNED,
                        traceId,
                        coreEvTaskTopic
                );
            }

            case COMPLETE_TASK -> kafkaProducer.publish(
                    service.completeTask(payload).mapDTO(),
                    CoreMessageType.TASK_COMPLETED,
                    traceId,
                    coreEvTaskTopic
            );
            case CREATE_TASK -> {
                CreateTaskRequest req = objectMapper.readValue(payload, CreateTaskRequest.class);
                kafkaProducer.publish(
                        service.createTask(
                                req.title(),
                                req.trackId(),
                                req.description()
                        ),
                        CoreMessageType.TASK_CREATED,
                        traceId,
                        coreEvTaskTopic);
            }
            case DELETE_TASK -> kafkaProducer.publish(
                    service.deleteTask(payload),
                    CoreMessageType.TASK_DELETED,
                    traceId,
                    coreEvTaskTopic
            );

            case UNDEFINED -> kafkaProducer.publishBodiless(CoreMessageType.UNDEFINED, traceId, coreEvTaskTopic);
            default -> throw new BadRequestException("Not request type");
        }
    }
}
