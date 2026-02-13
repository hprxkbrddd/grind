package com.grind.core.service.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grind.core.enums.CoreMessageType;
import com.grind.core.dto.wrap.Reply;
import com.grind.core.model.Sprint;
import com.grind.core.model.Track;
import com.grind.core.dto.request.track.ChangeTrackRequest;
import com.grind.core.dto.request.track.CreateTrackRequest;
import com.grind.core.service.application.TrackService;
import com.grind.core.util.ActionReplyExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrackReplyHandler {

    private final TrackService service;
    private final ObjectMapper objectMapper;
    private final ActionReplyExecutor exec;

    public Reply handleGetTracksOfUser() {
        return exec.withErrorMapping(() ->
                Reply.ok(
                        CoreMessageType.TRACKS_OF_USER,
                        service.getByUserId(
                                        SecurityContextHolder.getContext().getAuthentication().getName()
                                )
                                .stream()
                                .map(Track::mapDTO)
                                .toList()
                )
        );
    }

    public Reply handleGetTrack(String trackId) {
        return exec.withErrorMapping(() ->
                Reply.ok(
                        CoreMessageType.TRACK,
                        service.getById(trackId)
                                .mapDTO()
                )
        );
    }

    public Reply handleGetAllTracks() {
        return exec.withErrorMapping(() ->
                Reply.ok(CoreMessageType.ALL_TRACKS,
                        service.getAllTracks()
                                .stream().map(Track::mapDTO)
                )
        );
    }

    public Reply handleGetSprintsOfTrack(String trackId) {
        return exec.withErrorMapping(() ->
                Reply.ok(
                        CoreMessageType.SPRINTS_OF_TRACK,
                        service.getSprintsOfTrack(trackId)
                                .stream().map(Sprint::mapDTO)
                )
        );
    }

    public Reply handleChangeTrack(String payload) {
        return exec.withErrorMapping(() -> {
            ChangeTrackRequest req = objectMapper.readValue(payload, ChangeTrackRequest.class);
            return Reply.ok(CoreMessageType.TRACK_CHANGED,
                    service.changeTrack(
                            req.id(),
                            req.name(),
                            req.description(),
                            req.petId(),
                            req.startDate(),
                            req.targetDate(),
                            req.sprintLength(),
                            req.messagePolicy(),
                            req.status()
                    ).mapDTO());
        });
    }

    public Reply handleCreateTrack(String payload) {
        return exec.withErrorMapping(() -> {
            CreateTrackRequest req = objectMapper.readValue(payload, CreateTrackRequest.class);
            return Reply.ok(CoreMessageType.TRACK_CHANGED,
                    service.createTrack(
                            req.name(),
                            req.description(),
                            req.petId(),
                            req.sprintLength(),
                            req.startDate(),
                            req.targetDate(),
                            req.messagePolicy(),
                            req.status()
                    ).mapDTO()
            );
        });
    }

    public Reply handleDeleteTrack(String trackId) {
        return exec.withErrorMapping(() ->
                Reply.ok(
                        CoreMessageType.TRACK_DELETED,
                        service.deleteTrack(trackId)
                )
        );
    }
}
