package com.grind.statistics.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConsumer {
    @Bean
    public NewTopic statsTopic() {
        return TopicBuilder.name("statistics.request")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @KafkaListener(id = "statistics-server", topics = "core.event")
    public void listen(String in) {
        System.out.println(in);
    }
}
