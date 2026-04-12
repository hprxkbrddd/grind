package com.grind.gateway.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.grind.gateway.dto.Body;
import com.grind.gateway.dto.core.track.ChangeTrackDTO;
import com.grind.gateway.dto.core.track.CreateTrackRequest;
import com.grind.gateway.dto.core.track.SprintWithCountDTO;
import com.grind.gateway.dto.core.track.TrackDTO;
import com.grind.gateway.dto.core.track.TrackWithCountDTO;
import com.grind.gateway.service.core.CoreTrackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/core/track")
@RequiredArgsConstructor
@ApiResponses({
    @ApiResponse(responseCode = "400", description = "Invalid request",
            content = @Content(schema = @Schema(implementation = String.class),
                    examples = {
                            @ExampleObject(name = "blankId", value = "Track id must be not null or blank"),
                            @ExampleObject(name = "invalidStatus", value = "No enum constant com.grind.core.enums.TrackStatus.INVALID")
                    })),
        @ApiResponse(responseCode = "403", description = "Access denied",
                content = @Content(schema = @Schema(implementation = String.class),
                        examples = @ExampleObject(name = "forbidden", value = "Access denied"))),
        @ApiResponse(responseCode = "404", description = "Resource not found",
                content = @Content(schema = @Schema(implementation = String.class),
                        examples = @ExampleObject(name = "trackNotFound", value = "There is not track with id:track-1"))),
    @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(schema = @Schema(implementation = String.class),
                    examples = {
                            @ExampleObject(name = "serverError", value = "Internal server error"),
                            @ExampleObject(name = "aggregateState",
                                    value = "Aggregate root ('Track') has no child entities ('Sprint')")
                    }))
})
public class CoreTrackController {

    private final CoreTrackService trackService;

    @GetMapping
    @Operation(summary = "Get tracks of current user")
    @ApiResponse(responseCode = "200", description = "User tracks",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = TrackWithCountDTO.class)),
                    examples = @ExampleObject(
                            name = "userTracks",
                            value = "[{\"id\":\"track-1\",\"name\":\"Fitness\",\"description\":\"Workout plan\",\"petId\":\"pet-1\",\"durationDays\":30,\"tasks\":5,\"startDate\":\"2024-05-01\",\"targetDate\":\"2024-05-30\",\"createdAt\":\"2024-05-01T08:00:00\",\"messagePolicy\":\"null\",\"status\":\"ACTIVE\",\"userId\":\"user-1\"}]"
                    )))
    public ResponseEntity<?> getTracksOfUser() {
        Body<?> body = trackService.callGetTracksOfUser();
        return ResponseEntity.status(body.status())
                .body(body.error() == null ? body.payload() : body.error());
    }

    @GetMapping("/all")
    @Operation(summary = "Get all tracks")
    @ApiResponse(responseCode = "200", description = "All tracks",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = TrackWithCountDTO.class)),
                    examples = @ExampleObject(
                            name = "allTracks",
                            value = "[{\"id\":\"track-1\",\"name\":\"Fitness\",\"description\":\"Workout plan\",\"petId\":\"pet-1\",\"durationDays\":30,\"tasks\":5,\"startDate\":\"2024-05-01\",\"targetDate\":\"2024-05-30\",\"createdAt\":\"2024-05-01T08:00:00\",\"messagePolicy\":\"null\",\"status\":\"ACTIVE\",\"userId\":\"user-1\"}]"
                    )))
    public ResponseEntity<?> getAllTracks() {
        Body<?> body = trackService.callGetAllTracks();
        return ResponseEntity.status(body.status())
                .body(body.error() == null ? body.payload() : body.error());
    }

    @GetMapping("/{trackId}")
    @Operation(summary = "Get track by id")
    @ApiResponse(responseCode = "200", description = "Track details",
            content = @Content(schema = @Schema(implementation = TrackWithCountDTO.class),
                    examples = @ExampleObject(
                            name = "track",
                            value = "{\"id\":\"track-1\",\"name\":\"Fitness\",\"description\":\"Workout plan\",\"petId\":\"pet-1\",\"durationDays\":30,\"tasks\":5,\"startDate\":\"2024-05-01\",\"targetDate\":\"2024-05-30\",\"createdAt\":\"2024-05-01T08:00:00\",\"messagePolicy\":\"null\",\"status\":\"ACTIVE\",\"userId\":\"user-1\"}"
                    )))
    public ResponseEntity<?> getTrack(@PathVariable String trackId) {
        Body<?> body = trackService.callGetTrack(trackId);
        return ResponseEntity.status(body.status())
                .body(body.error() == null ? body.payload() : body.error());
    }

    @GetMapping("/sprints/{trackId}")
    @Operation(summary = "Get sprints of track")
    @ApiResponse(responseCode = "200", description = "Track sprints",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = SprintWithCountDTO.class)),
                    examples = @ExampleObject(
                            name = "sprints",
                            value = "[{\"id\":\"sprint-1\",\"startDate\":\"2024-05-01\",\"endDate\":\"2024-05-14\",\"track_id\":\"track-1\",\"tasks\":3}]"
                    )))
    public ResponseEntity<?> getSprintsOfTrack(@PathVariable String trackId) {
        Body<?> body = trackService.callGetSprintsOfTrack(trackId);
        return ResponseEntity.status(body.status())
                .body(body.error() == null ? body.payload() : body.error());
    }

    @PostMapping
    @Operation(summary = "Create track")
    @ApiResponse(responseCode = "200", description = "Track created",
            content = @Content(schema = @Schema(implementation = TrackDTO.class),
                    examples = @ExampleObject(
                            name = "created",
                            value = "{\"id\":\"track-1\",\"name\":\"Fitness\",\"description\":\"Workout plan\",\"petId\":\"pet-1\",\"durationDays\":30,\"startDate\":\"2024-05-01\",\"targetDate\":\"2024-05-30\",\"createdAt\":\"2024-05-01T08:00:00\",\"messagePolicy\":\"null\",\"status\":\"ACTIVE\",\"userId\":\"user-1\"}"
                    )))
    public ResponseEntity<?> create(@RequestBody CreateTrackRequest dto) {
        Body<?> body = trackService.callCreateTrack(dto);
        return ResponseEntity.status(body.status())
                .body(body.error() == null ? body.payload() : body.error());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update track")
    @ApiResponse(responseCode = "200", description = "Track updated",
            content = @Content(schema = @Schema(implementation = TrackDTO.class),
                    examples = @ExampleObject(
                            name = "updated",
                            value = "{\"id\":\"track-1\",\"name\":\"Fitness\",\"description\":\"Updated plan\",\"petId\":\"pet-1\",\"durationDays\":30,\"startDate\":\"2024-05-01\",\"targetDate\":\"2024-05-30\",\"createdAt\":\"2024-05-01T08:00:00\",\"messagePolicy\":\"null\",\"status\":\"ACTIVE\",\"userId\":\"user-1\"}"
                    )))
    public ResponseEntity<?> changeTrack(@RequestBody ChangeTrackDTO dto, @PathVariable String id) throws JsonProcessingException {
        dto.setId(id);
        Body<?> body = trackService.callChangeTrack(dto);
        return ResponseEntity.status(body.status())
                .body(body.error() == null ? body.payload() : body.error());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete track")
    @ApiResponse(responseCode = "200", description = "Track deleted",
            content = @Content(schema = @Schema(implementation = TrackDTO.class),
                    examples = @ExampleObject(
                            name = "deleted",
                            value = "{\"id\":\"track-1\",\"name\":\"Fitness\",\"description\":\"Workout plan\",\"petId\":\"pet-1\",\"durationDays\":30,\"startDate\":\"2024-05-01\",\"targetDate\":\"2024-05-30\",\"createdAt\":\"2024-05-01T08:00:00\",\"messagePolicy\":\"null\",\"status\":\"ACTIVE\",\"userId\":\"user-1\"}"
                    )))
    public ResponseEntity<?> delete(@PathVariable String id) {
        Body<?> body = trackService.callDeleteTrack(id);
        return ResponseEntity.status(body.status())
                .body(body.error() == null ? body.payload() : body.error());
    }
}
