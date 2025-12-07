package com.grind.gateway.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grind.gateway.dto.core.ChangeTaskDTO;
import com.grind.gateway.dto.core.CoreMessageDTO;
import com.grind.gateway.dto.core.TaskCreationDTO;
import com.grind.gateway.enums.CoreMessageType;
import com.grind.gateway.service.KafkaProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/api/core/task")
@RequiredArgsConstructor
public class CoreTaskController {

    // TODO get rid of repeating code
    private final KafkaProducer kafkaProducer;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topic.core.request.task}")
    private String coreReqTaskTopic;

    @GetMapping("/{taskId}")
    public ResponseEntity<Object> getTask(@PathVariable String taskId) throws TimeoutException {
        String correlationId = UUID.randomUUID().toString();
        kafkaProducer.publish(
                new CoreMessageDTO(
                        CoreMessageType.GET_TASK,
                        taskId
                ),
                coreReqTaskTopic,
                correlationId
        );

        return ResponseEntity.ok(
                kafkaProducer.retrieveResponse(correlationId)
        );
    }

    @GetMapping("/sprint/{sprintId}")
    public ResponseEntity<Object> getTasksOfSprint(@PathVariable String sprintId) throws TimeoutException {
        String correlationId = UUID.randomUUID().toString();
        kafkaProducer.publish(
                new CoreMessageDTO(
                        CoreMessageType.GET_TASKS_OF_SPRINT,
                        sprintId
                ),
                coreReqTaskTopic,
                correlationId
        );
        return ResponseEntity.ok(
                kafkaProducer.retrieveResponse(correlationId)
        );
    }

    @GetMapping("/track/{trackId}")
    public ResponseEntity<Object> getTasksOfTrack(@PathVariable String trackId) throws TimeoutException {
        String correlationId = UUID.randomUUID().toString();
        kafkaProducer.publish(
                new CoreMessageDTO(
                        CoreMessageType.GET_TASKS_OF_TRACK,
                        trackId
                ),
                coreReqTaskTopic,
                correlationId
        );
        return ResponseEntity.ok(
                kafkaProducer.retrieveResponse(correlationId)
        );
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody TaskCreationDTO dto) throws JsonProcessingException {
        String correlationId = UUID.randomUUID().toString();
        String payload = objectMapper.writeValueAsString(dto);

        kafkaProducer.publish(
                new CoreMessageDTO(
                        CoreMessageType.CREATE_TASK,
                        payload
                ),
                coreReqTaskTopic,
                correlationId
        );

        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> changeTask(@RequestBody ChangeTaskDTO dto, @PathVariable String id) throws JsonProcessingException {
        String correlationId = UUID.randomUUID().toString();
        dto.setId(id);
        String payload = objectMapper.writeValueAsString(dto);

        kafkaProducer.publish(
                new CoreMessageDTO(
                        CoreMessageType.CHANGE_TASK,
                        payload
                ),
                coreReqTaskTopic,
                correlationId
        );

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        String correlationId = UUID.randomUUID().toString();

        kafkaProducer.publish(
                new CoreMessageDTO(
                        CoreMessageType.DELETE_TASK,
                        id
                ),
                coreReqTaskTopic,
                correlationId
        );

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
