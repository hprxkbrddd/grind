package com.grind.gateway.controller;

import com.grind.gateway.dto.CoreMessageDTO;
import com.grind.gateway.service.KafkaProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/core")
@RequiredArgsConstructor
public class CoreController {

    private final KafkaProducer kafkaProducer;

    @Value("${kafka.topic.core.request}")
    private String coreReqTopic;


    @GetMapping("/ping2")
    public ResponseEntity<String> test() {
        String correlationId = UUID.randomUUID().toString();
        kafkaProducer.publish("pipipipipng1", coreReqTopic, correlationId);
        return ResponseEntity.ok(correlationId);
    }

    @GetMapping("/test/dto")
    public ResponseEntity<String> test2(@RequestBody CoreMessageDTO dto){
        String correlationId = UUID.randomUUID().toString();
        kafkaProducer.publish(dto, coreReqTopic, correlationId);
        return ResponseEntity.ok(correlationId);
    }
}
