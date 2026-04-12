package com.grind.statistics.service.handler;

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
                new ActionReplyExecutor()
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
}
