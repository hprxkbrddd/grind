package com.grind.gateway.controller;

import com.grind.gateway.dto.Body;
import com.grind.gateway.dto.statistics.DateRangeDTO;
import com.grind.gateway.dto.statistics.DiagramDTO;
import com.grind.gateway.dto.statistics.DiagramRangeRequestDTO;
import com.grind.gateway.dto.statistics.SprintStatsDTO;
import com.grind.gateway.dto.statistics.TrackActualStateStatsDTO;
import com.grind.gateway.dto.statistics.TrackRawStatsDTO;
import com.grind.gateway.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/statistics")
@Tag(name = "Statistics API")
@ApiResponses({
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Access denied",
                content = @Content(schema = @Schema(implementation = String.class),
                        examples = @ExampleObject(name = "forbidden", value = "Access denied"))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = String.class),
                        examples = {
                                @ExampleObject(name = "internalError", value = "Could not handle Kafka response: Internal server error"),
                                @ExampleObject(name = "serializationError", value = "Request serialization exception")
                        }))
})
public class StatisticsController {

    private final StatisticsService statisticsService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/sync-dbs")
    @Operation(summary = "Request statistics database synchronization")
    @ApiResponse(responseCode = "200", description = "Synchronization request accepted",
            content = @Content(schema = @Schema(implementation = String.class),
                    examples = @ExampleObject(name = "syncRequested", value = "Database sync request sent")))
    public ResponseEntity<String> syncDatabases() {
        statisticsService.syncDatabases();
        return ResponseEntity.ok("Database sync request sent");
    }

    @GetMapping("/track/{trackId}/state")
    @Operation(summary = "Get track statistics by actual task state")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Track state statistics",
                    content = @Content(schema = @Schema(implementation = TrackActualStateStatsDTO.class),
                            examples = @ExampleObject(
                                    name = "trackState",
                                    value = "{\"track_id\":\"track-1\",\"total_tasks\":42,\"completed_tasks\":18,\"remaining_tasks\":24,\"overdue_tasks\":5,\"active_wip\":19,\"completion_percent\":42.86,\"overdue_percent\":11.90,\"overdue_among_active_percent\":20.83,\"avg_active_age_days\":6.5}"
                            ))),
            @ApiResponse(responseCode = "400", description = "Invalid request or statistics not found",
                    content = @Content(schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(name = "trackMissing", value = "track id is not in stats db"))),
            @ApiResponse(responseCode = "504", description = "Statistics service timeout",
                    content = @Content(schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(name = "timeout", value = "Gateway timeout exceeded")))
    })
    public ResponseEntity<?> getTrackStatsState(@PathVariable String trackId) {
        Body<?> body = statisticsService.callGetTrackStatsActualState(trackId);
        return ResponseEntity.status(body.status())
                .body(body.error() == null ?
                        body.payload() : body.error()
                );
    }

    @GetMapping("/track/{trackId}/raw")
    @Operation(summary = "Get raw track completion statistics")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Track raw statistics",
                    content = @Content(schema = @Schema(implementation = TrackRawStatsDTO.class),
                            examples = @ExampleObject(
                                    name = "trackRaw",
                                    value = "{\"track_id\":\"track-1\",\"completed_last_30d\":12,\"completed_last_7d\":4}"
                            ))),
            @ApiResponse(responseCode = "400", description = "Invalid request or statistics not found",
                    content = @Content(schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(name = "trackMissing", value = "track id is not in stats db"))),
            @ApiResponse(responseCode = "504", description = "Statistics service timeout",
                    content = @Content(schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(name = "timeout", value = "Gateway timeout exceeded")))
    })
    public ResponseEntity<?> getTrackStatsRaw(@PathVariable String trackId) {
        Body<?> body = statisticsService.callGetTrackStatsRaw(trackId);
        return ResponseEntity.status(body.status())
                .body(body.error() == null ?
                        body.payload() : body.error()
                );
    }

    @GetMapping("/sprint/{sprintId}")
    @Operation(summary = "Get sprint statistics by actual task state")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sprint statistics",
                    content = @Content(schema = @Schema(implementation = SprintStatsDTO.class),
                            examples = @ExampleObject(
                                    name = "sprintStats",
                                    value = "{\"sprint_id\":\"sprint-1\",\"total_tasks\":20,\"completed_tasks\":9,\"remaining_tasks\":11,\"overdue_tasks\":3,\"active_wip\":8,\"completion_percent\":45.00,\"overdue_percent\":15.00,\"overdue_among_active_percent\":27.27,\"avg_active_age_days\":4.2}"
                            ))),
            @ApiResponse(responseCode = "400", description = "Invalid request or statistics not found",
                    content = @Content(schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(name = "sprintMissing", value = "sprint id is not in stats db"))),
            @ApiResponse(responseCode = "504", description = "Statistics service timeout",
                    content = @Content(schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(name = "timeout", value = "Gateway timeout exceeded")))
    })
    public ResponseEntity<?> getSprintStats(@PathVariable String sprintId) {
        Body<?> body = statisticsService.callGetSprintStats(sprintId);
        return ResponseEntity.status(body.status())
                .body(body.error() == null ?
                        body.payload() : body.error()
                );
    }

    @GetMapping("/track/{trackId}/per-day")
    @Operation(summary = "Get per-day track diagram statistics")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Per-day diagram statistics",
                    content = @Content(schema = @Schema(implementation = DiagramDTO.class),
                            examples = @ExampleObject(
                                    name = "perDayDiagram",
                                    value = "{\"diagram\":[{\"day\":\"2026-03-01\",\"completed_tasks\":0,\"planned_tasks\":2},{\"day\":\"2026-03-03\",\"completed_tasks\":1,\"planned_tasks\":3}]}"
                            ))),
            @ApiResponse(responseCode = "400", description = "Invalid request or statistics not found",
                    content = @Content(schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(name = "trackMissing", value = "track id is not in stats db"))),
            @ApiResponse(responseCode = "504", description = "Statistics service timeout",
                    content = @Content(schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(name = "timeout", value = "Gateway timeout exceeded")))
    })
    public ResponseEntity<?> getTrackStatsPerDay(@PathVariable String trackId) {
        Body<?> body = statisticsService.callGetTrackStatsPerDay(trackId);
        return ResponseEntity.status(body.status())
                .body(body.error() == null ?
                        body.payload() : body.error()
                );
    }

    @GetMapping("/track/{trackId}/per-week")
    @Operation(summary = "Get per-week track diagram statistics")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Per-week diagram statistics",
                    content = @Content(schema = @Schema(implementation = DiagramDTO.class),
                            examples = @ExampleObject(
                                    name = "perWeekDiagram",
                                    value = "{\"diagram\":[{\"day\":\"2026-03-02\",\"completed_tasks\":2,\"planned_tasks\":5},{\"day\":\"2026-03-09\",\"completed_tasks\":4,\"planned_tasks\":6}]}"
                            ))),
            @ApiResponse(responseCode = "400", description = "Invalid request or statistics not found",
                    content = @Content(schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(name = "trackMissing", value = "track id is not in stats db"))),
            @ApiResponse(responseCode = "504", description = "Statistics service timeout",
                    content = @Content(schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(name = "timeout", value = "Gateway timeout exceeded")))
    })
    public ResponseEntity<?> getTrackStatsPerWeek(@PathVariable String trackId) {
        Body<?> body = statisticsService.callGetTrackStatsPerWeek(trackId);
        return ResponseEntity.status(body.status())
                .body(body.error() == null ?
                        body.payload() : body.error()
                );
    }

    @GetMapping("/track/{trackId}/per-day/range")
    @Operation(
            summary = "Get per-day track diagram statistics within an exclusive date range",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = false,
                    description = "Exclusive date range passed to the gateway request body",
                    content = @Content(schema = @Schema(implementation = DateRangeDTO.class),
                            examples = @ExampleObject(
                                    name = "dayRange",
                                    value = "{\"startDate\":\"2026-03-01\",\"endDate\":\"2026-03-31\"}"
                            ))
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Per-day diagram statistics in the requested range",
                    content = @Content(schema = @Schema(implementation = DiagramDTO.class),
                            examples = @ExampleObject(
                                    name = "perDayRangeDiagram",
                                    value = "{\"diagram\":[{\"day\":\"2026-03-03\",\"completed_tasks\":1,\"planned_tasks\":3},{\"day\":\"2026-03-10\",\"completed_tasks\":2,\"planned_tasks\":4}]}"
                            ))),
            @ApiResponse(responseCode = "400", description = "Invalid request or statistics not found",
                    content = @Content(schema = @Schema(implementation = String.class),
                            examples = {
                                    @ExampleObject(name = "trackMissing", value = "track id is not in stats db"),
                                    @ExampleObject(name = "invalidRange", value = "Invalid request range")
                            })),
            @ApiResponse(responseCode = "504", description = "Statistics service timeout",
                    content = @Content(schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(name = "timeout", value = "Gateway timeout exceeded")))
    })
    public ResponseEntity<?> getTrackStatsPerDayInRange(
            @PathVariable String trackId,
            @RequestBody(required = false) DateRangeDTO range
    ) {
        Body<?> body = statisticsService.callGetTrackStatsPerDayInRange(
                DiagramRangeRequestDTO.of(trackId, range)
        );
        return ResponseEntity.status(body.status())
                .body(body.error() == null ?
                        body.payload() : body.error()
                );
    }

    @GetMapping("/track/{trackId}/per-week/range")
    @Operation(
            summary = "Get per-week track diagram statistics within an exclusive date range",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = false,
                    description = "Exclusive date range passed to the gateway request body",
                    content = @Content(schema = @Schema(implementation = DateRangeDTO.class),
                            examples = @ExampleObject(
                                    name = "weekRange",
                                    value = "{\"startDate\":\"2026-03-01\",\"endDate\":\"2026-03-31\"}"
                            ))
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Per-week diagram statistics in the requested range",
                    content = @Content(schema = @Schema(implementation = DiagramDTO.class),
                            examples = @ExampleObject(
                                    name = "perWeekRangeDiagram",
                                    value = "{\"diagram\":[{\"day\":\"2026-03-02\",\"completed_tasks\":2,\"planned_tasks\":5},{\"day\":\"2026-03-09\",\"completed_tasks\":4,\"planned_tasks\":6}]}"
                            ))),
            @ApiResponse(responseCode = "400", description = "Invalid request or statistics not found",
                    content = @Content(schema = @Schema(implementation = String.class),
                            examples = {
                                    @ExampleObject(name = "trackMissing", value = "track id is not in stats db"),
                                    @ExampleObject(name = "invalidRange", value = "Invalid request range")
                            })),
            @ApiResponse(responseCode = "504", description = "Statistics service timeout",
                    content = @Content(schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(name = "timeout", value = "Gateway timeout exceeded")))
    })
    public ResponseEntity<?> getTrackStatsPerWeekInRange(
            @PathVariable String trackId,
            @RequestBody(required = false) DateRangeDTO range
    ) {
        Body<?> body = statisticsService.callGetTrackStatsPerWeekInRange(
                DiagramRangeRequestDTO.of(trackId, range)
        );
        return ResponseEntity.status(body.status())
                .body(body.error() == null ?
                        body.payload() : body.error()
                );
    }
}
