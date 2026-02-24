package com.grind.gateway.service.application;

import com.grind.gateway.dto.Body;
import com.grind.gateway.dto.IdDTO;
import com.grind.gateway.enums.StatisticsMessageType;
import com.grind.gateway.service.kafka.KafkaProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrackStatisticsService {
    private final KafkaProducer kafkaProducer;

    @Value("${kafka.topic.statistics.request}")
    private String statReqTopic;

    public Body<?> callGetTrackCompletion(String trackId) {
        return kafkaProducer.requestReply(
                IdDTO.of(trackId),
                StatisticsMessageType.GET_TRACK_COMPLETION.name(),
                statReqTopic
        );
    }

    public Body<?> callGetTrackRemainingLoad(String trackId) {
        return kafkaProducer.requestReply(
                IdDTO.of(trackId),
                StatisticsMessageType.GET_TRACK_REMAINING_LOAD.name(),
                statReqTopic
        );
    }

    public Body<?> callGetTrackOverduePressure(String trackId) {
        return kafkaProducer.requestReply(
                IdDTO.of(trackId),
                StatisticsMessageType.GET_TRACK_OVERDUE_PRESSURE.name(),
                statReqTopic
        );
    }

    public Body<?> callGetTrackActiveTasksAging(String trackId) {
        return kafkaProducer.requestReply(
                IdDTO.of(trackId),
                StatisticsMessageType.GET_TRACK_ACTIVE_TASKS_AGING.name(),
                statReqTopic
        );
    }

    public Body<?> callGetTrackWorkInProgress(String trackId) {
        return kafkaProducer.requestReply(
                IdDTO.of(trackId),
                StatisticsMessageType.GET_TRACK_WORK_IN_PROGRESS.name(),
                statReqTopic
        );
    }

    public Body<?> callGetTrackOverdueAmongCompleted(String trackId) {
        return kafkaProducer.requestReply(
                IdDTO.of(trackId),
                StatisticsMessageType.GET_TRACK_OVERDUE_AMONG_COMPLETED.name(),
                statReqTopic
        );
    }

    public Body<?> callGetCompletedLastMonth(String trackId) {
        return kafkaProducer.requestReply(
                IdDTO.of(trackId),
                StatisticsMessageType.GET_COMPLETED_LAST_MONTH.name(),
                statReqTopic
        );
    }

    public Body<?> callGetCompletedLastWeek(String trackId) {
        return kafkaProducer.requestReply(
                IdDTO.of(trackId),
                StatisticsMessageType.GET_COMPLETED_LAST_WEEK.name(),
                statReqTopic
        );
    }
}
