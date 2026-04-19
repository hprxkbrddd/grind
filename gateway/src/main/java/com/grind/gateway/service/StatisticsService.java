package com.grind.gateway.service;

import com.grind.gateway.dto.Body;
import com.grind.gateway.dto.IdDTO;
import com.grind.gateway.dto.statistics.DiagramRangeRequestDTO;
import com.grind.gateway.enums.StatisticsMessageType;
import com.grind.gateway.service.kafka.KafkaProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Sends statistics requests from the gateway to the statistics service.
 */
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

    public Body<?> callGetTrackStatsPerDay(String trackId) {
        return kafkaProducer.requestReply(
                IdDTO.of(trackId),
                StatisticsMessageType.GET_STATS_PER_DAY.name(),
                statReqTopic
        );
    }

    public Body<?> callGetTrackStatsPerWeek(String trackId) {
        return kafkaProducer.requestReply(
                IdDTO.of(trackId),
                StatisticsMessageType.GET_STATS_PER_WEEK.name(),
                statReqTopic
        );
    }

    public Body<?> callGetTrackStatsPerDayInRange(DiagramRangeRequestDTO dto) {
        return kafkaProducer.requestReply(
                dto,
                StatisticsMessageType.GET_STATS_PER_DAY_IN_RANGE.name(),
                statReqTopic
        );
    }

    public Body<?> callGetTrackStatsPerWeekInRange(DiagramRangeRequestDTO dto) {
        return kafkaProducer.requestReply(
                dto,
                StatisticsMessageType.GET_STATS_PER_WEEK_IN_RANGE.name(),
                statReqTopic
        );
    }

    public void syncDatabases(){
        kafkaProducer.requestReply(
                null,
                StatisticsMessageType.SYNC_DATABASES.name(),
                statReqTopic
        );
    }
}
