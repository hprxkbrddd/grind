package com.grind.gateway.controller;

import com.grind.gateway.dto.Body;
import com.grind.gateway.service.application.SprintStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/statistics/sprint/{sprintId}")
public class SprintStatisticsController {

    private final SprintStatisticsService sprintStatisticsService;

    @GetMapping("/completion")
    public ResponseEntity<?> getTrackCompletion(@PathVariable String sprintId) {
        Body<?> body = sprintStatisticsService.callGetSprintCompletion(sprintId);
        return ResponseEntity.status(body.status())
                .body(body.error()==null ?
                        body.payload() : body.error()
                );
    }

    @GetMapping("/remaining-load")
    public ResponseEntity<?> getRemainingLoad(@PathVariable String sprintId) {
        Body<?> body = sprintStatisticsService.callGetSprintRemainingLoad(sprintId);
        return ResponseEntity.status(body.status())
                .body(body.error()==null ?
                        body.payload() : body.error()
                );
    }

    @GetMapping("/overdue-pressure")
    public ResponseEntity<?> getOverduePressure(@PathVariable String sprintId) {
        Body<?> body = sprintStatisticsService.callGetSprintOverduePressure(sprintId);
        return ResponseEntity.status(body.status())
                .body(body.error()==null ?
                        body.payload() : body.error()
                );
    }

    @GetMapping("/active-task-aging")
    public ResponseEntity<?> getActiveTasksAging(@PathVariable String sprintId) {
        Body<?> body = sprintStatisticsService.callGetSprintActiveTasksAging(sprintId);
        return ResponseEntity.status(body.status())
                .body(body.error()==null ?
                        body.payload() : body.error()
                );
    }

    @GetMapping("/work-in-progress")
    public ResponseEntity<?> getWorkInProgress(@PathVariable String sprintId) {
        Body<?> body = sprintStatisticsService.callGetSprintWorkInProgress(sprintId);
        return ResponseEntity.status(body.status())
                .body(body.error()==null ?
                        body.payload() : body.error()
                );
    }

    @GetMapping("/overdue-among-completed")
    public ResponseEntity<?> getOverdueAmongCompleted(@PathVariable String sprintId) {
        Body<?> body = sprintStatisticsService.callGetSprintOverdueAmongCompleted(sprintId);
        return ResponseEntity.status(body.status())
                .body(body.error()==null ?
                        body.payload() : body.error()
                );
    }
}
