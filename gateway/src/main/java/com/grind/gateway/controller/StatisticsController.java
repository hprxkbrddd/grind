package com.grind.gateway.controller;

import com.grind.gateway.dto.Body;
import com.grind.gateway.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/statistics/track/{trackId}")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/completion")
    public ResponseEntity<?> getTrackCompletion(@PathVariable String trackId) {
        Body<?> body = statisticsService.callGetTrackCompletion(trackId);
        return ResponseEntity.status(body.status())
                .body(body.error()==null ?
                        body.payload() : body.error()
                );
    }

    @GetMapping("/remaining-load")
    public ResponseEntity<?> getOverduePressure(@PathVariable String trackId) {
        Body<?> body = statisticsService.callGetOverduePressure(trackId);
        return ResponseEntity.status(body.status())
                .body(body.error()==null ?
                        body.payload() : body.error()
                );
    }

    @GetMapping("/active-task-aging")
    public ResponseEntity<?> getActiveTasksAging(@PathVariable String trackId) {
        Body<?> body = statisticsService.callGetActiveTasksAging(trackId);
        return ResponseEntity.status(body.status())
                .body(body.error()==null ?
                        body.payload() : body.error()
                );
    }

    @GetMapping("/work-in-progress")
    public ResponseEntity<?> getWorkInProgress(@PathVariable String trackId) {
        Body<?> body = statisticsService.callGetWorkInProgress(trackId);
        return ResponseEntity.status(body.status())
                .body(body.error()==null ?
                        body.payload() : body.error()
                );
    }

    @GetMapping("/overdue-among-completed")
    public ResponseEntity<?> getOverdueAmongCompleted(@PathVariable String trackId) {
        Body<?> body = statisticsService.callGetOverdueAmongCompleted(trackId);
        return ResponseEntity.status(body.status())
                .body(body.error()==null ?
                        body.payload() : body.error()
                );
    }
    @GetMapping("/completed-last-month")
    public ResponseEntity<?> getCompletedLastMonth(@PathVariable String trackId) {
        Body<?> body = statisticsService.callGetCompletedLastMonth(trackId);
        return ResponseEntity.status(body.status())
                .body(body.error()==null ?
                        body.payload() : body.error()
                );
    }
}
