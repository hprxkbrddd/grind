package com.grind.statistics.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${kafka.topic.statistics.request}")
    private String statReq;
    @Value("${kafka.topic.statistics.response}")
    private String statRes;

    @Bean
    public NewTopic statsReqTopic() {
        return TopicBuilder.name(statReq)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic statsResTopic() {
        return TopicBuilder.name(statRes)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
