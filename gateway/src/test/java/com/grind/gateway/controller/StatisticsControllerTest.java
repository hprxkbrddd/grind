package com.grind.gateway.controller;

import com.grind.gateway.dto.Body;
import com.grind.gateway.dto.statistics.DateRangeDTO;
import com.grind.gateway.dto.statistics.DiagramDTO;
import com.grind.gateway.dto.statistics.DiagramRangeRequestDTO;
import com.grind.gateway.service.StatisticsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StatisticsControllerTest {

    @Mock
    private StatisticsService statisticsService;

    @InjectMocks
    private StatisticsController controller;

    @Test
    void getTrackStatsPerDayInRange_shouldUseBodyRange() {
        DiagramDTO payload = new DiagramDTO(List.of());
        Body<?> body = Body.ok(payload);
        doReturn(body).when(statisticsService)
                .callGetTrackStatsPerDayInRange(org.mockito.ArgumentMatchers.any());

        DateRangeDTO range = new DateRangeDTO(
                LocalDate.of(2026, 3, 1),
                LocalDate.of(2026, 3, 31)
        );

        ResponseEntity<?> response = controller.getTrackStatsPerDayInRange(
                "track-1",
                range
        );

        ArgumentCaptor<DiagramRangeRequestDTO> captor = ArgumentCaptor.forClass(DiagramRangeRequestDTO.class);
        verify(statisticsService).callGetTrackStatsPerDayInRange(captor.capture());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(payload, response.getBody());
        assertEquals("track-1", captor.getValue().trackId());
        assertEquals(LocalDate.of(2026, 3, 1), captor.getValue().startDate());
        assertEquals(LocalDate.of(2026, 3, 31), captor.getValue().endDate());
    }

    @Test
    void getTrackStatsPerWeekInRange_shouldUseBodyRange() {
        DiagramDTO payload = new DiagramDTO(List.of());
        Body<?> body = Body.ok(payload);
        doReturn(body).when(statisticsService)
                .callGetTrackStatsPerWeekInRange(org.mockito.ArgumentMatchers.any());

        DateRangeDTO range = new DateRangeDTO(
                LocalDate.of(2026, 4, 1),
                LocalDate.of(2026, 4, 30)
        );

        ResponseEntity<?> response = controller.getTrackStatsPerWeekInRange(
                "track-2",
                range
        );

        ArgumentCaptor<DiagramRangeRequestDTO> captor = ArgumentCaptor.forClass(DiagramRangeRequestDTO.class);
        verify(statisticsService).callGetTrackStatsPerWeekInRange(captor.capture());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(payload, response.getBody());
        assertEquals("track-2", captor.getValue().trackId());
        assertEquals(LocalDate.of(2026, 4, 1), captor.getValue().startDate());
        assertEquals(LocalDate.of(2026, 4, 30), captor.getValue().endDate());
    }

    @Test
    void getTrackStatsPerDayInRange_shouldForwardInvalidRangeToStatisticsService() {
        DiagramDTO payload = new DiagramDTO(List.of());
        Body<?> body = Body.ok(payload);
        doReturn(body).when(statisticsService)
                .callGetTrackStatsPerDayInRange(org.mockito.ArgumentMatchers.any());

        ResponseEntity<?> response = controller.getTrackStatsPerDayInRange(
                "track-1",
                new DateRangeDTO(
                        LocalDate.of(2026, 3, 31),
                        LocalDate.of(2026, 3, 1)
                )
        );

        ArgumentCaptor<DiagramRangeRequestDTO> captor = ArgumentCaptor.forClass(DiagramRangeRequestDTO.class);
        verify(statisticsService).callGetTrackStatsPerDayInRange(captor.capture());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(LocalDate.of(2026, 3, 31), captor.getValue().startDate());
        assertEquals(LocalDate.of(2026, 3, 1), captor.getValue().endDate());
    }

    @Test
    void getTrackStatsPerWeekInRange_shouldAllowMissingBodyForStatisticsFallback() {
        DiagramDTO payload = new DiagramDTO(List.of());
        Body<?> body = Body.ok(payload);
        doReturn(body).when(statisticsService)
                .callGetTrackStatsPerWeekInRange(org.mockito.ArgumentMatchers.any());

        ResponseEntity<?> response = controller.getTrackStatsPerWeekInRange("track-2", null);

        ArgumentCaptor<DiagramRangeRequestDTO> captor = ArgumentCaptor.forClass(DiagramRangeRequestDTO.class);
        verify(statisticsService).callGetTrackStatsPerWeekInRange(captor.capture());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("track-2", captor.getValue().trackId());
        assertEquals(null, captor.getValue().startDate());
        assertEquals(null, captor.getValue().endDate());
    }
}
