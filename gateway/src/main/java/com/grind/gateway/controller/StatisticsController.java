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
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/track/{trackId}/state")
    public ResponseEntity<?> getTrackStatsState(@PathVariable String trackId) {
        Body<?> body = statisticsService.callGetTrackStatsActualState(trackId);
        return ResponseEntity.status(body.status())
                .body(body.error()==null ?
                        body.payload() : body.error()
                );
    }

    @GetMapping("/track/{trackId}/raw")
    public ResponseEntity<?> getTrackStatsRaw(@PathVariable String trackId) {
        Body<?> body = statisticsService.callGetTrackStatsRaw(trackId);
        return ResponseEntity.status(body.status())
                .body(body.error()==null ?
                        body.payload() : body.error()
                );
    }

    @GetMapping("/sprint/{sprintId}")
    public ResponseEntity<?> getSprintStats(@PathVariable String sprintId) {
        Body<?> body = statisticsService.callGetSprintStats(sprintId);
        return ResponseEntity.status(body.status())
                .body(body.error()==null ?
                        body.payload() : body.error()
                );
    }
}
