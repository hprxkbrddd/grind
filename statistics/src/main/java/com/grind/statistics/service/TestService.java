package com.grind.statistics.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.grind.statistics.dto.CoreRecord;
import com.grind.statistics.repository.ClickhouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestService {

    private final ClickhouseRepository repository;

    public void insert(CoreRecord rec) throws JsonProcessingException {
        repository.postEvent(List.of(rec));
    }
}
