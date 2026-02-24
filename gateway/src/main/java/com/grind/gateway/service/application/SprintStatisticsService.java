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
public class SprintStatisticsService {
    private final KafkaProducer kafkaProducer;

    @Value("${kafka.topic.statistics.request}")
    private String statReqTopic;

    public Body<?> callGetSprintCompletion(String trackId) {
        return kafkaProducer.requestReply(
                IdDTO.of(trackId),
                StatisticsMessageType.GET_SPRINT_COMPLETION.name(),
                statReqTopic
        );
    }

    public Body<?> callGetSprintRemainingLoad(String trackId) {
        return kafkaProducer.requestReply(
                IdDTO.of(trackId),
                StatisticsMessageType.GET_SPRINT_REMAINING_LOAD.name(),
                statReqTopic
        );
    }

    public Body<?> callGetSprintOverduePressure(String trackId) {
        return kafkaProducer.requestReply(
                IdDTO.of(trackId),
                StatisticsMessageType.GET_SPRINT_OVERDUE_PRESSURE.name(),
                statReqTopic
        );
    }

    public Body<?> callGetSprintActiveTasksAging(String trackId) {
        return kafkaProducer.requestReply(
                IdDTO.of(trackId),
                StatisticsMessageType.GET_SPRINT_ACTIVE_TASKS_AGING.name(),
                statReqTopic
        );
    }

    public Body<?> callGetSprintWorkInProgress(String trackId) {
        return kafkaProducer.requestReply(
                IdDTO.of(trackId),
                StatisticsMessageType.GET_SPRINT_WORK_IN_PROGRESS.name(),
                statReqTopic
        );
    }

    public Body<?> callGetSprintOverdueAmongCompleted(String trackId) {
        return kafkaProducer.requestReply(
                IdDTO.of(trackId),
                StatisticsMessageType.GET_SPRINT_OVERDUE_AMONG_COMPLETED.name(),
                statReqTopic
        );
    }

}
