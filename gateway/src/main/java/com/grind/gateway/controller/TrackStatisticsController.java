package com.grind.gateway.controller;

import com.grind.gateway.dto.Body;
import com.grind.gateway.service.application.TrackStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/statistics/track/{trackId}")
public class TrackStatisticsController {

    private final TrackStatisticsService trackStatisticsService;

    @GetMapping("/completion")
    public ResponseEntity<?> getTrackCompletion(@PathVariable String trackId) {
        Body<?> body = trackStatisticsService.callGetTrackCompletion(trackId);
        return ResponseEntity.status(body.status())
                .body(body.error()==null ?
                        body.payload() : body.error()
                );
    }

    @GetMapping("/remaining-load")
    public ResponseEntity<?> getRemainingLoad(@PathVariable String trackId) {
        Body<?> body = trackStatisticsService.callGetTrackRemainingLoad(trackId);
        return ResponseEntity.status(body.status())
                .body(body.error()==null ?
                        body.payload() : body.error()
                );
    }

    @GetMapping("/overdue-pressure")
    public ResponseEntity<?> getOverduePressure(@PathVariable String trackId) {
        Body<?> body = trackStatisticsService.callGetTrackOverduePressure(trackId);
        return ResponseEntity.status(body.status())
                .body(body.error()==null ?
                        body.payload() : body.error()
                );
    }

    @GetMapping("/active-task-aging")
    public ResponseEntity<?> getActiveTasksAging(@PathVariable String trackId) {
        Body<?> body = trackStatisticsService.callGetTrackActiveTasksAging(trackId);
        return ResponseEntity.status(body.status())
                .body(body.error()==null ?
                        body.payload() : body.error()
                );
    }

    @GetMapping("/work-in-progress")
    public ResponseEntity<?> getWorkInProgress(@PathVariable String trackId) {
        Body<?> body = trackStatisticsService.callGetTrackWorkInProgress(trackId);
        return ResponseEntity.status(body.status())
                .body(body.error()==null ?
                        body.payload() : body.error()
                );
    }

    @GetMapping("/overdue-among-completed")
    public ResponseEntity<?> getOverdueAmongCompleted(@PathVariable String trackId) {
        Body<?> body = trackStatisticsService.callGetTrackOverdueAmongCompleted(trackId);
        return ResponseEntity.status(body.status())
                .body(body.error()==null ?
                        body.payload() : body.error()
                );
    }

    @GetMapping("/completed-last-month")
    public ResponseEntity<?> getCompletedLastMonth(@PathVariable String trackId) {
        Body<?> body = trackStatisticsService.callGetCompletedLastMonth(trackId);
        return ResponseEntity.status(body.status())
                .body(body.error()==null ?
                        body.payload() : body.error()
                );
    }

    @GetMapping("/completed-last-week")
    public ResponseEntity<?> getCompletedLastWeek(@PathVariable String trackId) {
        Body<?> body = trackStatisticsService.callGetCompletedLastWeek(trackId);
        return ResponseEntity.status(body.status())
                .body(body.error()==null ?
                        body.payload() : body.error()
                );
    }
}
