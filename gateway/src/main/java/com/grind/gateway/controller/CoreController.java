package com.grind.gateway.controller;

import com.grind.gateway.service.KafkaProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/core")
@RequiredArgsConstructor
public class CoreController {

    private final KafkaProducer kafkaProducer;

    @GetMapping("/ping2")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok(
                kafkaProducer.callWithTimeout(
                                "ping2",
                                UUID.randomUUID().toString(),
                                "core.request")
                        .toString()
        );
    }
}
