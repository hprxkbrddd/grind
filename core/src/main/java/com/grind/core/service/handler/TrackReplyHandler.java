package com.grind.core.service.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grind.core.dto.entity.SprintDTO;
import com.grind.core.dto.entity.TrackDTO;
import com.grind.core.dto.request.track.ChangeTrackRequest;
import com.grind.core.dto.request.track.CreateTrackRequest;
import com.grind.core.dto.wrap.Reply;
import com.grind.core.enums.CoreMessageType;
import com.grind.core.enums.coreMessageTypes.CoreTrackReqMsgType;
import com.grind.core.enums.coreMessageTypes.CoreTrackResMsgType;
import com.grind.core.model.Sprint;
import com.grind.core.model.Track;
import com.grind.core.service.application.TrackService;
import com.grind.core.util.ActionReplyExecutor;
import com.grind.core.util.IdParser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrackReplyHandler {

    private final TrackService service;
    private final ObjectMapper objectMapper;
    private final ActionReplyExecutor exec;

    public Reply<?> routeReply(CoreTrackReqMsgType type, String payload) {
        switch (type) {
            case GET_TRACKS_OF_USER -> {
                return handleGetTracksOfUser();
            }
            case GET_TRACK -> {
                return handleGetTrack(payload);
            }
            case GET_ALL_TRACKS -> {
                return handleGetAllTracks();
            }
            case GET_SPRINTS_OF_TRACK -> {
                return handleGetSprintsOfTrack(payload);
            }
            case CHANGE_TRACK -> {
                return handleChangeTrack(payload);
            }
            case CREATE_TRACK -> {
                return handleCreateTrack(payload);
            }
            case DELETE_TRACK -> {
                return handleDeleteTrack(payload);
            }
            default -> throw new UnsupportedOperationException("Message type is not related to tracks");
        }
    }

    private Reply<List<TrackDTO>> handleGetTracksOfUser() {
        return exec.withErrorMapping(() ->
                Reply.ok(
                        CoreTrackResMsgType.TRACKS_OF_USER,
                        service.getByUserId(
                                        SecurityContextHolder.getContext().getAuthentication().getName()
                                )
                                .stream()
                                .map(Track::mapDTO)
                                .toList()
                )
        );
    }

    private Reply<TrackDTO> handleGetTrack(String payload) {
        return exec.withErrorMapping(() ->
                Reply.ok(
                        CoreTrackResMsgType.TRACK,
                        service.getById(
                                IdParser.run(payload)
                        ).mapDTO()
                )
        );
    }

    private Reply<List<TrackDTO>> handleGetAllTracks() {
        return exec.withErrorMapping(() ->
                Reply.ok(CoreTrackResMsgType.ALL_TRACKS,
                        service.getAllTracks()
                                .stream()
                                .map(Track::mapDTO)
                                .toList()
                )
        );
    }

    private Reply<List<SprintDTO>> handleGetSprintsOfTrack(String payload) {
        return exec.withErrorMapping(() ->
                Reply.ok(
                        CoreTrackResMsgType.SPRINTS_OF_TRACK,
                        service.getSprintsOfTrack(
                                IdParser.run(payload)
                        ).stream().map(Sprint::mapDTO).toList()
                )
        );
    }

    private Reply<TrackDTO> handleChangeTrack(String payload) {
        return exec.withErrorMapping(() -> {
            ChangeTrackRequest req = objectMapper.readValue(payload, ChangeTrackRequest.class);
            return Reply.ok(CoreTrackResMsgType.TRACK_CHANGED,
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

    private Reply<TrackDTO> handleCreateTrack(String payload) {
        return exec.withErrorMapping(() -> {
            CreateTrackRequest req = objectMapper.readValue(payload, CreateTrackRequest.class);
            return Reply.ok(CoreTrackResMsgType.TRACK_CREATED,
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

    private Reply<TrackDTO> handleDeleteTrack(String payload) {
        return exec.withErrorMapping(() ->
                Reply.ok(
                        CoreTrackResMsgType.TRACK_DELETED,
                        service.deleteTrack(
                                IdParser.run(payload)
                        ).mapDTO()
                )
        );
    }
}
