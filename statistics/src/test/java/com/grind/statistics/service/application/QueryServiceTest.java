package com.grind.statistics.service.application;

import com.grind.statistics.dto.response.diagram.DiagramDTO;
import com.grind.statistics.dto.response.diagram.DiagramUnit;
import com.grind.statistics.repository.ClickhouseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.grind.statistics.repository.ClickhouseQueries.Q_STATS_PER_DAY;
import static com.grind.statistics.repository.ClickhouseQueries.Q_STATS_PER_DAY_IN_RANGE;
import static com.grind.statistics.repository.ClickhouseQueries.Q_STATS_PER_WEEK;
import static com.grind.statistics.repository.ClickhouseQueries.Q_STATS_PER_WEEK_IN_RANGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QueryServiceTest {

    @Mock
    private ClickhouseRepository repository;

    @Test
    void getDiagramDataPerDayInRange_shouldUseRangeQueryAndPassParameters() {
        QueryService queryService = new QueryService(repository);
        LocalDate startDate = LocalDate.of(2026, 1, 1);
        LocalDate endDate = LocalDate.of(2026, 1, 31);
        DiagramUnit unit = new DiagramUnit(LocalDate.of(2026, 1, 15), 2, 5);

        when(repository.requestSelect(
                eq(Q_STATS_PER_DAY_IN_RANGE),
                eq(Map.of(
                        "param_track", "track-1",
                        "param_startDate", "2026-01-01",
                        "param_endDate", "2026-01-31"
                )),
                eq(DiagramUnit.class)
        )).thenReturn(Flux.just(unit));

        DiagramDTO result = queryService.getDiagramDataPerDayInRange("track-1", startDate, endDate);

        assertEquals(List.of(unit), result.diagram());
        verify(repository).requestSelect(
                eq(Q_STATS_PER_DAY_IN_RANGE),
                eq(Map.of(
                        "param_track", "track-1",
                        "param_startDate", "2026-01-01",
                        "param_endDate", "2026-01-31"
                )),
                eq(DiagramUnit.class)
        );
    }

    @Test
    void getDiagramDataPerWeekInRange_shouldUseRangeQueryAndPassParameters() {
        QueryService queryService = new QueryService(repository);
        LocalDate startDate = LocalDate.of(2026, 2, 1);
        LocalDate endDate = LocalDate.of(2026, 2, 28);
        DiagramUnit unit = new DiagramUnit(LocalDate.of(2026, 2, 3), 4, 7);

        when(repository.requestSelect(
                eq(Q_STATS_PER_WEEK_IN_RANGE),
                eq(Map.of(
                        "param_track", "track-2",
                        "param_startDate", "2026-02-01",
                        "param_endDate", "2026-02-28"
                )),
                eq(DiagramUnit.class)
        )).thenReturn(Flux.just(unit));

        DiagramDTO result = queryService.getDiagramDataPerWeekInRange("track-2", startDate, endDate);

        assertEquals(List.of(unit), result.diagram());
        verify(repository).requestSelect(
                eq(Q_STATS_PER_WEEK_IN_RANGE),
                eq(Map.of(
                        "param_track", "track-2",
                        "param_startDate", "2026-02-01",
                        "param_endDate", "2026-02-28"
                )),
                eq(DiagramUnit.class)
        );
    }

    @Test
    void getDiagramDataPerDayInRange_shouldFallbackToNonRangeQueryWhenRangeIsInvalid() {
        QueryService queryService = new QueryService(repository);
        DiagramUnit unit = new DiagramUnit(LocalDate.of(2026, 1, 15), 2, 5);

        when(repository.requestSelect(
                eq(Q_STATS_PER_DAY),
                eq(Map.of("param_track", "track-1")),
                eq(DiagramUnit.class)
        )).thenReturn(Flux.just(unit));

        DiagramDTO result = queryService.getDiagramDataPerDayInRange(
                "track-1",
                LocalDate.of(2026, 2, 1),
                LocalDate.of(2026, 1, 1)
        );

        assertEquals(List.of(unit), result.diagram());
        verify(repository).requestSelect(
                eq(Q_STATS_PER_DAY),
                eq(Map.of("param_track", "track-1")),
                eq(DiagramUnit.class)
        );
    }

    @Test
    void getDiagramDataPerWeekInRange_shouldFallbackToNonRangeQueryWhenRangeIsMissing() {
        QueryService queryService = new QueryService(repository);
        DiagramUnit unit = new DiagramUnit(LocalDate.of(2026, 2, 3), 4, 7);

        when(repository.requestSelect(
                eq(Q_STATS_PER_WEEK),
                eq(Map.of("param_track", "track-2")),
                eq(DiagramUnit.class)
        )).thenReturn(Flux.just(unit));

        DiagramDTO result = queryService.getDiagramDataPerWeekInRange("track-2", null, LocalDate.of(2026, 2, 28));

        assertEquals(List.of(unit), result.diagram());
        verify(repository).requestSelect(
                eq(Q_STATS_PER_WEEK),
                eq(Map.of("param_track", "track-2")),
                eq(DiagramUnit.class)
        );
    }

    @Test
    void rangeQueries_shouldUseOpenBoundsAndContainExpectedPlaceholders() {
        assertTrue(Q_STATS_PER_DAY_IN_RANGE.contains("{track:UUID}"));
        assertTrue(Q_STATS_PER_DAY_IN_RANGE.contains("{startDate:DATE}"));
        assertTrue(Q_STATS_PER_DAY_IN_RANGE.contains("{endDate:DATE}"));
        assertTrue(Q_STATS_PER_DAY_IN_RANGE.contains("HAVING day > {startDate:DATE} AND day < {endDate:DATE}"));

        assertTrue(Q_STATS_PER_WEEK_IN_RANGE.contains("{track:UUID}"));
        assertTrue(Q_STATS_PER_WEEK_IN_RANGE.contains("{startDate:DATE}"));
        assertTrue(Q_STATS_PER_WEEK_IN_RANGE.contains("{endDate:DATE}"));
        assertTrue(Q_STATS_PER_WEEK_IN_RANGE.contains("HAVING day > {startDate:DATE} AND day < {endDate:DATE}"));
    }
}
