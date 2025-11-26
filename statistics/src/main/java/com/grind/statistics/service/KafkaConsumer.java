package com.grind.statistics.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Configuration
@RequiredArgsConstructor
public class KafkaConsumer {

    private final KafkaProducer kafkaProducer;

    @KafkaListener(id = "statistics-server", topics = "core.events")
    public void listen(String in) {
        System.out.println("triggered core.events listener. publishing response...");
        kafkaProducer.publish("processed pong");
    }
}
