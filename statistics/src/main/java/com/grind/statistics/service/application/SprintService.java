package com.grind.statistics.service.application;

import com.grind.statistics.dto.response.sprint.*;
import com.grind.statistics.repository.ClickhouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.grind.statistics.repository.SprintQueries.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class SprintService {
    private final ClickhouseRepository repository;
    public SprintCompletionDTO getSprintCompletion(String sprintId) {
        List<SprintCompletionDTO> list = repository.requestSelect(
                Q_SPRINT_COMPLETION,
                Map.of("param_sprint", sprintId),
                SprintCompletionDTO.class
        ).collectList().block();

        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("sprint id is not in stats db");
        }

        return list.get(0);
    }

    public SprintRemainingLoadDTO getRemainingLoad(String sprintId) {
        List<SprintRemainingLoadDTO> list = repository.requestSelect(
                Q_SPRINT_REMAINING_LOAD,
                Map.of("param_sprint", sprintId),
                SprintRemainingLoadDTO.class
        ).collectList().block();

        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("sprint id is not in stats db");
        }

        return list.get(0);
    }

    public SprintOverduePressureDTO getOverduePressure(String sprintId) {
        List<SprintOverduePressureDTO> list = repository.requestSelect(
                Q_SPRINT_OVERDUE_PRESSURE,
                Map.of("param_sprint", sprintId),
                SprintOverduePressureDTO.class
        ).collectList().block();

        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("sprint id is not in stats db");
        }

        return list.get(0);
    }

    public SprintActiveTasksAgingDTO getActiveTasksAging(String sprintId) {
        List<SprintActiveTasksAgingDTO> list = repository.requestSelect(
                Q_SPRINT_ACTIVE_TASKS_AGING,
                Map.of("param_sprint", sprintId),
                SprintActiveTasksAgingDTO.class
        ).collectList().block();

        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("sprint id is not in stats db");
        }

        return list.get(0);
    }

    public SprintWIPDTO getWIP(String sprintId) {
        List<SprintWIPDTO> list = repository.requestSelect(
                Q_SPRINT_WORK_IN_PROGRESS,
                Map.of("param_sprint", sprintId),
                SprintWIPDTO.class
        ).collectList().block();

        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("sprint id is not in stats db");
        }

        return list.get(0);
    }

    public SprintOverdueAmongCompletedDTO getOverdueAmongCompleted(String sprintId) {
        List<SprintOverdueAmongCompletedDTO> list = repository.requestSelect(
                Q_SPRINT_OVERDUE_AMONG_COMPLETED,
                Map.of("param_sprint", sprintId),
                SprintOverdueAmongCompletedDTO.class
        ).collectList().block();

        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("sprint id is not in stats db");
        }

        return list.get(0);
    }
}
