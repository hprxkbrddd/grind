package com.grind.gateway.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grind.gateway.dto.Body;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaProducerTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;
    @Mock
    private ObjectMapper objectMapper;

    private PendingRegistry pendingRegistry;
    private KafkaProducer kafkaProducer;

    @BeforeEach
    void setUp() {
        pendingRegistry = new PendingRegistry();
        kafkaProducer = new KafkaProducer(kafkaTemplate, pendingRegistry, objectMapper);
        ReflectionTestUtils.setField(kafkaProducer, "responseTimeoutMs", 1L);
    }

    @Test
    void requestReply_shouldReturnSerializationErrorWhenObjectMapperFails() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(any()))
                .thenThrow(new JsonProcessingException("boom") { });

        Body<?> response = kafkaProducer.requestReply(new Object(), "TYPE", "topic");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.status());
        assertEquals("Request serialization exception", response.error());
        verifyNoInteractions(kafkaTemplate);
    }

    @Test
    void retrieveResponse_shouldReturnTimeoutAndCleanRegistry() {
        pendingRegistry.put("cid", new CompletableFuture<>());

        Body<?> response = kafkaProducer.retrieveResponse("cid");

        assertEquals(HttpStatus.GATEWAY_TIMEOUT, response.status());
        assertEquals("Gateway timeout exceeded", response.error());
        assertNull(pendingRegistry.get("cid"));
    }
}
