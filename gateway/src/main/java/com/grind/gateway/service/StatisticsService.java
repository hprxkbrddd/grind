package com.grind.gateway.service;

import com.grind.gateway.dto.Body;
import com.grind.gateway.dto.IdDTO;
import com.grind.gateway.enums.StatisticsMessageType;
import com.grind.gateway.service.kafka.KafkaProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatisticsService {
    private final KafkaProducer kafkaProducer;

    @Value("${kafka.topic.statistics.request}")
    private String statReqTopic;

    public Body<?> callGetTrackStatsActualState(String trackId) {
        return kafkaProducer.requestReply(
                IdDTO.of(trackId),
                StatisticsMessageType.GET_TRACK_STATS_ACTUAL_STATE.name(),
                statReqTopic
        );
    }

    public Body<?> callGetTrackStatsRaw(String trackId) {
        return kafkaProducer.requestReply(
                IdDTO.of(trackId),
                StatisticsMessageType.GET_TRACK_STATS_RAW.name(),
                statReqTopic
        );
    }

    public Body<?> callGetSprintStats(String trackId) {
        return kafkaProducer.requestReply(
                IdDTO.of(trackId),
                StatisticsMessageType.GET_SPRINT_STATS.name(),
                statReqTopic
        );
    }
}
