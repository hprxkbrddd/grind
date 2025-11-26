package com.grind.gateway.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${kafka.topic.response}")
    private String response;

    @Bean
    public NewTopic statsReqTopic() {
        return TopicBuilder.name(response)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
