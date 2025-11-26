package com.grind.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;

import java.time.Duration;

@Configuration
public class KafkaRpcConfig {

    @Value("${kafka.topic.response}")
    private String response;

    // контейнер, который слушает топик с ответами
    @Bean
    public KafkaMessageListenerContainer<String, Object> coreRepliesContainer(
            ConsumerFactory<String, Object> consumerFactory) {

        ContainerProperties containerProps = new ContainerProperties(response);
        containerProps.setGroupId("gateway-rpc-group");

        return new KafkaMessageListenerContainer<>(consumerFactory, containerProps);
    }

    // ReplyingKafkaTemplate — именно он делает request-reply
    @Bean
    public ReplyingKafkaTemplate<String, Object, Object> replyingKafkaTemplate(
            ProducerFactory<String, Object> pf,
            KafkaMessageListenerContainer<String, Object> repliesContainer) {

        ReplyingKafkaTemplate<String, Object, Object> template =
                new ReplyingKafkaTemplate<>(pf, repliesContainer);

        // дефолтный таймаут ожидания ответа
        template.setDefaultReplyTimeout(Duration.ofMillis(500));

        return template;
    }
}
