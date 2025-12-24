package com.grind.statistics.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${kafka.topic.statistics.request}")
    private String statReq;
    @Value("${kafka.topic.statistics.event}")
    private String statEv;

    @Bean
    public NewTopic statsReqTopic() {
        return TopicBuilder.name(statReq)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic statsEvTopic() {
        return TopicBuilder.name(statEv)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
