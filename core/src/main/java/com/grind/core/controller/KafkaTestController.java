package com.grind.core.controller;

import com.grind.core.service.KafkaProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <h1>Тестовый контроллер</h1>
 */
@RestController
@RequiredArgsConstructor
public class KafkaTestController {

    private final KafkaProducer kafkaProducer;

    @GetMapping("/ping")
    public void ping(){
        kafkaProducer.publish("pong", null, "core.event.task");
    }

    @GetMapping("/pingOrdered")
    public void pingOrdered(){
        kafkaProducer.publish("pongOrdered", "to the same partition plzzzz", null, "core.event.task");
    }


    @GetMapping("/megaping")
    public void megaPing(){
        kafkaProducer.publishOrdered(List.of("megapong", "megapong", "megapong", "megapong", "megapong", "megapong", "megapong", "megapong", "megapong"), null, "core.event.task");
    }

    @GetMapping("/pingxtimes")
    public void pingXtimes(){
        kafkaProducer.publish(List.of("ping for x times", "ping for x times", "ping for x times", "ping for x times", "ping for x times", "ping for x times"), null, "core.event.task");
    }
}
