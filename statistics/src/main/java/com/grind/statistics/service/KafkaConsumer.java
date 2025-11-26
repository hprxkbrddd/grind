package com.grind.statistics.service;

import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;

@Configuration
public class KafkaConsumer {

    @KafkaListener(id = "statistics-server", topics = "core.event")
    public void listen(String in) {
        System.out.println("processing core.event message.....");
        System.out.println("message '"+in+"' is processed");
    }
}
