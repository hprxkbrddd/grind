package com.grind.core.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grind.core.dto.request.IdDTO;
import org.springframework.stereotype.Component;

@Component
public class IdParser {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String run(String idDTO) {
        try {
            return objectMapper.readValue(idDTO, IdDTO.class).id();
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Passed string is not an 'IdDTO' JSON object");
        }
    }
}
