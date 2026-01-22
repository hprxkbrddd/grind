package com.grind.gateway.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grind.gateway.dto.core.ChangeTrackDTO;
import com.grind.gateway.dto.core.CreateTrackRequest;
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
@RequestMapping("/api/core/track")
@RequiredArgsConstructor
public class CoreTrackController {

    // TODO get rid of repeating code
    private final KafkaProducer kafkaProducer;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topic.core.request.task}")
    private String coreReqTaskTopic;

    @GetMapping
    public ResponseEntity<Object> getTracksOfUser() throws TimeoutException {
        String correlationId = UUID.randomUUID().toString();
        kafkaProducer.publishBodiless(
                CoreMessageType.GET_TRACKS_OF_USER,
                coreReqTaskTopic,
                correlationId
        );
        return ResponseEntity.ok(
                kafkaProducer.retrieveResponse(correlationId)
        );
    }

    @GetMapping("/{trackId}")
    public ResponseEntity<Object> getTrack(@PathVariable String trackId) throws TimeoutException {
        String correlationId = UUID.randomUUID().toString();
        kafkaProducer.publish(
                trackId,
                CoreMessageType.GET_TRACK,
                coreReqTaskTopic,
                correlationId
        );
        return ResponseEntity.ok(
                kafkaProducer.retrieveResponse(correlationId)
        );
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody CreateTrackRequest dto) throws JsonProcessingException {
        String correlationId = UUID.randomUUID().toString();
        String payload = objectMapper.writeValueAsString(dto);

        kafkaProducer.publish(
                payload,
                CoreMessageType.CREATE_TRACK,
                coreReqTaskTopic,
                correlationId
        );

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> changeTrack(@RequestBody ChangeTrackDTO dto, @PathVariable String id) throws JsonProcessingException {
        String correlationId = UUID.randomUUID().toString();
        dto.setId(id);
        String payload = objectMapper.writeValueAsString(dto);

        kafkaProducer.publish(
                payload,
                CoreMessageType.CHANGE_TRACK,
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
                CoreMessageType.DELETE_TRACK,
                coreReqTaskTopic,
                correlationId
        );

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
