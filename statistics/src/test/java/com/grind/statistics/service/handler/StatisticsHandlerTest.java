package com.grind.statistics.service.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grind.statistics.dto.response.diagram.DiagramDTO;
import com.grind.statistics.dto.response.diagram.DiagramUnit;
import com.grind.statistics.dto.response.track.TrackRawStatsDTO;
import com.grind.statistics.dto.wrap.Reply;
import com.grind.statistics.enums.StatisticsMessageType;
import com.grind.statistics.service.application.QueryService;
import com.grind.statistics.service.application.SynchronizationService;
import com.grind.statistics.util.ActionReplyExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatisticsHandlerTest {

    @Mock
    private QueryService queryService;
    @Mock
    private SynchronizationService synchronizationService;

    private StatisticsHandler statisticsHandler;

    @BeforeEach
    void setUp() {
        statisticsHandler = new StatisticsHandler(
                queryService,
                synchronizationService,
                new ActionReplyExecutor(),
                new ObjectMapper().findAndRegisterModules()
        );
    }

    @Test
    void routeReply_shouldReturnRawTrackStats() {
        TrackRawStatsDTO payload = new TrackRawStatsDTO("track-1", 12L, 4L);
        when(queryService.getRawStats("track-1")).thenReturn(payload);

        Reply<?> reply = statisticsHandler.routeReply(
                StatisticsMessageType.GET_TRACK_STATS_RAW,
                "{\"id\":\"track-1\"}"
        );

        assertEquals(StatisticsMessageType.TRACK_STATS_RAW, reply.type());
        assertEquals(payload, reply.body().payload());
        verify(queryService).getRawStats("track-1");
    }

    @Test
    void routeReply_shouldTriggerSynchronization() {
        Reply<?> reply = statisticsHandler.routeReply(
                StatisticsMessageType.SYNC_DATABASES,
                ""
        );

        assertEquals(StatisticsMessageType.DATABASES_SYNCED, reply.type());
        verify(synchronizationService).synchronizeDatabases();
    }

    @Test
    void routeReply_shouldReturnDiagramStatsPerDayInRange() {
        DiagramDTO payload = new DiagramDTO(List.of(new DiagramUnit(LocalDate.of(2026, 3, 3), 1, 2)));
        when(queryService.getDiagramDataPerDayInRange(
                "track-1",
                LocalDate.of(2026, 3, 1),
                LocalDate.of(2026, 3, 31)
        )).thenReturn(payload);

        Reply<?> reply = statisticsHandler.routeReply(
                StatisticsMessageType.GET_STATS_PER_DAY_IN_RANGE,
                """
                {"track_id":"track-1","start_date":"2026-03-01","end_date":"2026-03-31"}
                """
        );

        assertEquals(StatisticsMessageType.STATS_PER_DAY_IN_RANGE, reply.type());
        assertEquals(payload, reply.body().payload());
        verify(queryService).getDiagramDataPerDayInRange(
                "track-1",
                LocalDate.of(2026, 3, 1),
                LocalDate.of(2026, 3, 31)
        );
    }

    @Test
    void routeReply_shouldReturnDiagramStatsPerWeekInRange() {
        DiagramDTO payload = new DiagramDTO(List.of(new DiagramUnit(LocalDate.of(2026, 4, 6), 3, 5)));
        when(queryService.getDiagramDataPerWeekInRange(
                "track-2",
                LocalDate.of(2026, 4, 1),
                LocalDate.of(2026, 4, 30)
        )).thenReturn(payload);

        Reply<?> reply = statisticsHandler.routeReply(
                StatisticsMessageType.GET_STATS_PER_WEEK_IN_RANGE,
                """
                {"track_id":"track-2","start_date":"2026-04-01","end_date":"2026-04-30"}
                """
        );

        assertEquals(StatisticsMessageType.STATS_PER_WEEK_IN_RANGE, reply.type());
        assertEquals(payload, reply.body().payload());
        verify(queryService).getDiagramDataPerWeekInRange(
                "track-2",
                LocalDate.of(2026, 4, 1),
                LocalDate.of(2026, 4, 30)
        );
    }
}
