package com.grind.gateway.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grind.gateway.dto.ChangeTrackDTO;
import com.grind.gateway.dto.CoreMessageDTO;
import com.grind.gateway.dto.CoreMessageType;
import com.grind.gateway.dto.TrackCreationDTO;
import com.grind.gateway.service.KafkaProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/core/track")
@RequiredArgsConstructor
public class CoreTrackController {

    // TODO get rid of repeating code
    private final KafkaProducer kafkaProducer;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topic.core.request.task}")
    private String coreReqTaskTopic;

    @GetMapping("/{trackId}")
    public ResponseEntity<String> getTrack(@PathVariable String trackId) {
        String correlationId = UUID.randomUUID().toString();
        kafkaProducer.publish(
                new CoreMessageDTO(
                        CoreMessageType.GET_TRACK,
                        trackId
                ),
                coreReqTaskTopic,
                correlationId
        );
        return ResponseEntity.ok(correlationId);
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody TrackCreationDTO dto) throws JsonProcessingException {
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
    public ResponseEntity<Void> changeTrack(@RequestBody ChangeTrackDTO dto, @PathVariable String id) throws JsonProcessingException {
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
