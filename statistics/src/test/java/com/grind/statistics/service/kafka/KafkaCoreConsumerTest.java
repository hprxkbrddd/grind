package com.grind.statistics.service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grind.statistics.dto.request.StatisticsEventDTO;
import com.grind.statistics.enums.TaskStatus;
import com.grind.statistics.service.application.QueryService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class KafkaCoreConsumerTest {

    @Mock
    private QueryService queryService;
    @Mock
    private Acknowledgment acknowledgment;

    @Test
    void consumeCoreRecords_shouldConvertDeletedEventAndAcknowledge() throws Exception {
        KafkaCoreConsumer consumer = new KafkaCoreConsumer(
                new ObjectMapper().findAndRegisterModules(),
                queryService
        );

        ConsumerRecord<String, String> record = new ConsumerRecord<>(
                "core.event.task",
                0,
                0L,
                "key",
                """
                {"track_id":"track-1","sprint_id":"sprint-1","task_id":"task-1","user_id":"user-1","planned_date":"2026-01-02","version":3,"task_status":"PLANNED","changed_at":"2026-01-03T10:15:30.123"}
                """
        );
        record.headers().add(new RecordHeader(
                "X-Event-Id",
                "15".getBytes(StandardCharsets.UTF_8)
        ));
        record.headers().add(new RecordHeader(
                "X-Message-Type",
                "TASK_DELETED".getBytes(StandardCharsets.UTF_8)
        ));

        consumer.consumeCoreRecords(List.of(record), acknowledgment);

        ArgumentCaptor<List<StatisticsEventDTO>> captor = ArgumentCaptor.forClass(List.class);
        verify(queryService).postEvent(captor.capture());
        verify(acknowledgment).acknowledge();

        StatisticsEventDTO event = captor.getValue().get(0);
        assertEquals(15L, event.eventId());
        assertEquals("track-1", event.trackId());
        assertEquals("task-1", event.taskId());
        assertEquals("user-1", event.userId());
        assertEquals(3L, event.version());
        assertEquals(TaskStatus.DELETED, event.taskStatus());
        assertNull(event.sprintId());
        assertNull(event.plannedDate());
        assertEquals(LocalDateTime.of(2026, 1, 3, 10, 15, 30, 123_000_000), event.changedAt());
    }

    @Test
    void consumeCoreRecords_shouldKeepOverdueStatusForOverdueEvent() throws Exception {
        KafkaCoreConsumer consumer = new KafkaCoreConsumer(
                new ObjectMapper().findAndRegisterModules(),
                queryService
        );

        ConsumerRecord<String, String> record = new ConsumerRecord<>(
                "core.event.task",
                0,
                0L,
                "key",
                """
                {"track_id":"track-1","sprint_id":"sprint-1","task_id":"task-1","user_id":"user-1","planned_date":"2026-01-02","version":4,"task_status":"OVERDUE","changed_at":"2026-01-03T10:15:30.123"}
                """
        );
        record.headers().add(new RecordHeader(
                "X-Event-Id",
                "16".getBytes(StandardCharsets.UTF_8)
        ));
        record.headers().add(new RecordHeader(
                "X-Message-Type",
                "TASK_OVERDUE".getBytes(StandardCharsets.UTF_8)
        ));

        consumer.consumeCoreRecords(List.of(record), acknowledgment);

        ArgumentCaptor<List<StatisticsEventDTO>> captor = ArgumentCaptor.forClass(List.class);
        verify(queryService).postEvent(captor.capture());
        verify(acknowledgment).acknowledge();

        StatisticsEventDTO event = captor.getValue().get(0);
        assertEquals(16L, event.eventId());
        assertEquals(TaskStatus.OVERDUE, event.taskStatus());
        assertEquals("sprint-1", event.sprintId());
        assertEquals("track-1", event.trackId());
    }

    @Test
    void consumeCoreRecords_shouldFailWhenEventIdHeaderMissing() {
        KafkaCoreConsumer consumer = new KafkaCoreConsumer(
                new ObjectMapper().findAndRegisterModules(),
                queryService
        );

        ConsumerRecord<String, String> record = new ConsumerRecord<>(
                "core.event.task",
                0,
                0L,
                "key",
                "{}"
        );
        record.headers().add(new RecordHeader(
                "X-Message-Type",
                "TASK_CREATED".getBytes(StandardCharsets.UTF_8)
        ));

        assertThrows(
                IllegalStateException.class,
                () -> consumer.consumeCoreRecords(List.of(record), acknowledgment)
        );
        verifyNoInteractions(queryService, acknowledgment);
    }
}
