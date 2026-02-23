package com.grind.statistics.controller;

import com.grind.statistics.dto.request.StatisticsEventDTO;
import com.grind.statistics.service.application.TrackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/test/stat")
@RequiredArgsConstructor
public class TestController {

    private final TrackService service;

    @PostMapping
    public ResponseEntity<Void> insert(@RequestBody List<StatisticsEventDTO> rec){
        service.postEvent(rec);
        return ResponseEntity.ok().build();
    }
}
