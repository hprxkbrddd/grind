package com.grind.gateway.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grind.gateway.dto.core.ChangeTaskDTO;
import com.grind.gateway.dto.core.CreateTaskRequest;
import com.grind.gateway.dto.core.PlanTaskDateDTO;
import com.grind.gateway.dto.core.PlanTaskSprintDTO;
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

    @GetMapping("/all")
    public ResponseEntity<Object> getAllTasks() throws TimeoutException {
        String correlationId = UUID.randomUUID().toString();
        kafkaProducer.publishBodiless(
                CoreMessageType.GET_ALL_TASKS,
                coreReqTaskTopic,
                correlationId
        );
        return ResponseEntity.ok(
                kafkaProducer.retrieveResponse(correlationId)
        );
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<Object> getTask(@PathVariable String taskId) throws TimeoutException {
        String correlationId = UUID.randomUUID().toString();
        kafkaProducer.publish(
                taskId,
                CoreMessageType.GET_TASK,
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
                sprintId,
                CoreMessageType.GET_TASKS_OF_SPRINT,
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
                trackId,
                CoreMessageType.GET_TASKS_OF_TRACK,
                coreReqTaskTopic,
                correlationId
        );
        return ResponseEntity.ok(
                kafkaProducer.retrieveResponse(correlationId)
        );
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody CreateTaskRequest dto) throws JsonProcessingException {
        String correlationId = UUID.randomUUID().toString();
        String payload = objectMapper.writeValueAsString(dto);

        kafkaProducer.publish(
                payload,
                CoreMessageType.CREATE_TASK,
                coreReqTaskTopic,
                correlationId
        );

        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> changeTask(@RequestBody ChangeTaskDTO dto, @PathVariable String id) throws JsonProcessingException {
        String correlationId = UUID.randomUUID().toString();
        dto.setTaskId(id);
        String payload = objectMapper.writeValueAsString(dto);

        kafkaProducer.publish(
                payload,
                CoreMessageType.CHANGE_TASK,
                coreReqTaskTopic,
                correlationId
        );

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/{taskId}/plan/sprint")
    public ResponseEntity<Void> planTaskSprint(@RequestBody PlanTaskSprintDTO dto, @PathVariable String taskId) throws JsonProcessingException {
        String correlationId = UUID.randomUUID().toString();
        dto.setTaskId(taskId);
        String payload = objectMapper.writeValueAsString(dto);

        kafkaProducer.publish(
                payload,
                CoreMessageType.PLAN_TASK_SPRINT,
                coreReqTaskTopic,
                correlationId
        );

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/{taskId}/plan/date")
    public ResponseEntity<Void> planTaskDate(@RequestBody PlanTaskDateDTO dto, @PathVariable String taskId) throws JsonProcessingException {
        String correlationId = UUID.randomUUID().toString();
        dto.setTaskId(taskId);
        String payload = objectMapper.writeValueAsString(dto);

        kafkaProducer.publish(
                payload,
                CoreMessageType.PLAN_TASK_DATE,
                coreReqTaskTopic,
                correlationId
        );

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/{taskId}/complete")
    public ResponseEntity<Void> completeTask(@PathVariable String taskId){
        String correlationId = UUID.randomUUID().toString();

        kafkaProducer.publish(
                taskId,
                CoreMessageType.COMPLETE_TASK,
                coreReqTaskTopic,
                correlationId
        );

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        String correlationId = UUID.randomUUID().toString();

        kafkaProducer.publish(
                id,
                CoreMessageType.DELETE_TASK,
                coreReqTaskTopic,
                correlationId
        );

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
