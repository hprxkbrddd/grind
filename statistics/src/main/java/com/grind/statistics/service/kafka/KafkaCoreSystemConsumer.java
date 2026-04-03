package com.grind.statistics.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KafkaCoreSystemConsumer {

    private final KafkaCoreConsumer kafkaCoreConsumer;

    @KafkaListener(
            containerFactory = "kafkaBatchListenerContainerFactory",
            topics = "${kafka.topic.core.system.response}"
    )
    public void listenCoreSystemResponse(
            List<ConsumerRecord<String, String>> records,
            Acknowledgment acknowledgment
    ) throws JsonProcessingException {
        kafkaCoreConsumer.consumeCoreRecords(records, acknowledgment);
    }
}
