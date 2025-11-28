package com.grind.core.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${kafka.topic.core.events}")
    private String coreEventTopic;
    @Value("${kafka.topic.core.request}")
    private String coreReqTopic;

    @Bean
    public NewTopic coreEventTopic(){
        return TopicBuilder
                .name(coreEventTopic)
                .partitions(3)
                .build();
    }

    @Bean
    public NewTopic coreReqTopic(){
        return TopicBuilder
                .name(coreReqTopic)
                .partitions(3)
                .build();
    }
}
