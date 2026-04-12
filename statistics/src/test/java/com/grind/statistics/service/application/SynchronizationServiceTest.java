package com.grind.statistics.service.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grind.statistics.service.kafka.KafkaProducer;
import com.grind.statistics.util.TraceContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SynchronizationServiceTest {

    @Mock
    private QueryService queryService;
    @Mock
    private KafkaProducer kafkaProducer;

    @AfterEach
    void tearDown() {
        TraceContext.setTraceId(null);
    }

    @Test
    void synchronizeDatabases_shouldPublishLastIngestedEventId() {
        SynchronizationService synchronizationService = new SynchronizationService(
                queryService,
                kafkaProducer,
                new ObjectMapper()
        );
        ReflectionTestUtils.setField(
                synchronizationService,
                "coreSystemRequestTopic",
                "core.system.request"
        );

        when(queryService.getLastEventId()).thenReturn(42L);
        TraceContext.setTraceId("trace-1");

        synchronizationService.synchronizeDatabases();

        verify(kafkaProducer).publish(
                "{\"lastIngestedEventId\":42}",
                "trace-1",
                "core.system.request"
        );
    }
}
