package com.grind.statistics.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.grind.statistics.dto.CoreRecord;
import com.grind.statistics.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test/stat")
@RequiredArgsConstructor
public class TestController {

    private final TestService service;

    @PostMapping
    public ResponseEntity<Void> insert(@RequestBody CoreRecord rec){
        try {
            service.insert(rec);
            return ResponseEntity.ok().build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
