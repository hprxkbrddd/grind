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

    public Body<?> callGetTrackCompletion(String trackId){
        return kafkaProducer.requestReply(
                IdDTO.of(trackId),
                StatisticsMessageType.GET_TRACK_COMPLETION.name(),
                statReqTopic
        );
    }

    public Body<?> callGetRemainingLoad(String trackId){
        return kafkaProducer.requestReply(
                IdDTO.of(trackId),
                StatisticsMessageType.GET_REMAINING_LOAD.name(),
                statReqTopic
        );
    }

    public Body<?> callGetOverduePressure(String trackId){
        return kafkaProducer.requestReply(
                IdDTO.of(trackId),
                StatisticsMessageType.GET_OVERDUE_PRESSURE.name(),
                statReqTopic
        );
    }

    public Body<?> callGetActiveTasksAging(String trackId){
        return kafkaProducer.requestReply(
                IdDTO.of(trackId),
                StatisticsMessageType.GET_ACTIVE_TASKS_AGING.name(),
                statReqTopic
        );
    }

    public Body<?> callGetWorkInProgress(String trackId){
        return kafkaProducer.requestReply(
                IdDTO.of(trackId),
                StatisticsMessageType.GET_WORK_IN_PROGRESS.name(),
                statReqTopic
        );
    }

    public Body<?> callGetOverdueAmongCompleted(String trackId){
        return kafkaProducer.requestReply(
                IdDTO.of(trackId),
                StatisticsMessageType.GET_OVERDUE_AMONG_COMPLETED.name(),
                statReqTopic
        );
    }

    public Body<?> callGetCompletedLastMonth(String trackId){
        return kafkaProducer.requestReply(
                IdDTO.of(trackId),
                StatisticsMessageType.GET_COMPLETED_LAST_MONTH.name(),
                statReqTopic
        );
    }
}
