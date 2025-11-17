package com.grind.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    /**
     * Publishes single message
     * @param value
     */
    public void publishMsg(String value){
        kafkaTemplate.send("core.events", value);
    }

    /**
     * Publishes a group of messages to the single partition <br>
     * That means messages will be published and consumed in turn (from first to last)
     * @param values
     */
    public void publishOrderedMsgs(List<String> values){
        for (String value : values){
            kafkaTemplate.send("core.events", UUID.randomUUID().toString(), value);
        }
    }
}
