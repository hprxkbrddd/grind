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

    @Value("${kafka.topic.core.request.task}")
    private String coreReqTaskTopic;
    @Value("${kafka.topic.core.event.task}")
    private String coreEvTaskTopic;

    @Value("${kafka.topic.core.request.track}")
    private String coreReqTrackTopic;
    @Value("${kafka.topic.core.event.track}")
    private String coreEvTrackTopic;

    @Value("${kafka.topic.core.system.request}")
    private String coreSystemReqTopic;
    @Value("${kafka.topic.core.system.response}")
    private String coreSystemRespTopic;

    @Value("${kafka.topic.statistics.request}")
    private String statReq;
    @Value("${kafka.topic.statistics.event}")
    private String statEv;

    @Bean
    public NewTopic response() {
        return TopicBuilder.name(response)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic coreReqTaskTopic(){
        return TopicBuilder
                .name(coreReqTaskTopic)
                .partitions(3)
                .build();
    }

    @Bean
    public NewTopic coreEvTaskTopic(){
        return TopicBuilder
                .name(coreEvTaskTopic)
                .partitions(3)
                .build();
    }

    @Bean
    public NewTopic coreReqTrackTopic(){
        return TopicBuilder
                .name(coreReqTrackTopic)
                .partitions(3)
                .build();
    }

    @Bean
    public NewTopic coreEvTrackTopic(){
        return TopicBuilder
                .name(coreEvTrackTopic)
                .partitions(3)
                .build();
    }

    @Bean
    public NewTopic coreSystemReqTopic() {
        return TopicBuilder
                .name(coreSystemReqTopic)
                .partitions(3)
                .build();
    }

    @Bean
    public NewTopic coreSystemRespTopic() {
        return TopicBuilder
                .name(coreSystemRespTopic)
                .partitions(3)
                .build();
    }

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
