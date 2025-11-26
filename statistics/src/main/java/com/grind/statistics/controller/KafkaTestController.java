package com.grind.statistics.controller;

import com.grind.statistics.service.KafkaProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <h1>Тестовый контроллер</h1>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/stats")
public class KafkaTestController {

    private final KafkaProducer kafkaProducer;

    @GetMapping("/ping")
    public void ping(){
        kafkaProducer.publish("pong");
    }

    @GetMapping("/pingOrdered")
    public void pingOrdered(){
        kafkaProducer.publish("pongOrdered", "to the same partition plzzzz");
    }


    @GetMapping("/megaping")
    public void megaPing(){
        kafkaProducer.publishOrdered(List.of("megapong", "megapong", "megapong", "megapong", "megapong", "megapong", "megapong", "megapong", "megapong"));
    }

    @GetMapping("/pingxtimes")
    public void pingXtimes(){
        kafkaProducer.publish(List.of("ping for x times", "ping for x times", "ping for x times", "ping for x times", "ping for x times", "ping for x times"));
    }
}