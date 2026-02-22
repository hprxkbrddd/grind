package com.grind.gateway.service.core;

import com.grind.gateway.dto.Body;
import com.grind.gateway.dto.IdDTO;
import com.grind.gateway.dto.core.task.ChangeTaskDTO;
import com.grind.gateway.dto.core.task.CreateTaskRequest;
import com.grind.gateway.dto.core.task.PlanTaskDateDTO;
import com.grind.gateway.dto.core.task.PlanTaskSprintDTO;
import com.grind.gateway.enums.CoreMessageType;
import com.grind.gateway.service.kafka.KafkaProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CoreTaskService {
    private final KafkaProducer kafkaProducer;

    @Value("${kafka.topic.core.request.task}")
    private String coreReqTaskTopic;

    public Body<?> callGetAllTasks() {
        return kafkaProducer.requestReply(
                null,
                CoreMessageType.GET_ALL_TASKS.name(),
                coreReqTaskTopic
        );
    }

    public Body<?> callGetTask(String taskId){
        return kafkaProducer.requestReply(
                IdDTO.of(taskId),
                CoreMessageType.GET_TASK.name(),
                coreReqTaskTopic
        );
    }

    public Body<?> callGetTasksOfSprint(String sprintId){
        return kafkaProducer.requestReply(
                IdDTO.of(sprintId),
                CoreMessageType.GET_TASKS_OF_SPRINT.name(),
                coreReqTaskTopic
        );
    }

    public Body<?> callGetTasksOfTrack(String trackId){
        return kafkaProducer.requestReply(
                IdDTO.of(trackId),
                CoreMessageType.GET_TASKS_OF_TRACK.name(),
                coreReqTaskTopic
        );
    }

    public Body<?> callCreateTask(CreateTaskRequest dto){
        return kafkaProducer.requestReply(
                dto,
                CoreMessageType.CREATE_TASK.name(),
                coreReqTaskTopic
        );
    }

    public Body<?> callChangeTask(ChangeTaskDTO dto){
        return kafkaProducer.requestReply(
                dto,
                CoreMessageType.CHANGE_TASK.name(),
                coreReqTaskTopic
        );
    }

    public Body<?> callPlanTaskSprint(PlanTaskSprintDTO dto){
        return kafkaProducer.requestReply(
                dto,
                CoreMessageType.PLAN_TASK_SPRINT.name(),
                coreReqTaskTopic
        );
    }

    public Body<?> callPlanTaskDate(PlanTaskDateDTO dto){
        return kafkaProducer.requestReply(
                dto,
                CoreMessageType.PLAN_TASK_DATE.name(),
                coreReqTaskTopic
        );
    }

    public Body<?> callCompleteTask(String taskId){
        return kafkaProducer.requestReply(
                IdDTO.of(taskId),
                CoreMessageType.COMPLETE_TASK.name(),
                coreReqTaskTopic
        );
    }

    public Body<?> callTaskToBacklog(String taskId){
        return kafkaProducer.requestReply(
                IdDTO.of(taskId),
                CoreMessageType.MOVE_TASK_TO_BACKLOG.name(),
                coreReqTaskTopic
        );
    }

    public Body<?> callDeleteTask(String taskId){
        return kafkaProducer.requestReply(
                IdDTO.of(taskId),
                CoreMessageType.DELETE_TASK.name(),
                coreReqTaskTopic
        );
    }
}
