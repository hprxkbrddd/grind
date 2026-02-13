package com.grind.gateway.service.core;

import com.grind.gateway.dto.Body;
import com.grind.gateway.dto.IdDTO;
import com.grind.gateway.dto.core.track.ChangeTrackDTO;
import com.grind.gateway.dto.core.track.CreateTrackRequest;
import com.grind.gateway.enums.CoreMessageType;
import com.grind.gateway.service.kafka.KafkaProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CoreTrackService {

    private final KafkaProducer kafkaProducer;

    @Value("${kafka.topic.core.request.track}")
    private String coreReqTrackTopic;

    public Body callGetAllTracks() {
        return kafkaProducer.requestReply(
                null,
                CoreMessageType.GET_ALL_TRACKS,
                coreReqTrackTopic
        );
    }
    public Body callGetTracksOfUser() {
        return kafkaProducer.requestReply(
                null,
                CoreMessageType.GET_TRACKS_OF_USER,
                coreReqTrackTopic
        );
    }

    public Body callGetTrack(String trackId){
        return kafkaProducer.requestReply(
                IdDTO.of(trackId),
                CoreMessageType.GET_TRACK,
                coreReqTrackTopic
        );
    }

    public Body callGetSprintsOfTrack(String trackId){
        return kafkaProducer.requestReply(
                IdDTO.of(trackId),
                CoreMessageType.GET_SPRINTS_OF_TRACK,
                coreReqTrackTopic
        );
    }

    public Body callCreateTrack(CreateTrackRequest dto){
        return kafkaProducer.requestReply(
                dto,
                CoreMessageType.CREATE_TRACK,
                coreReqTrackTopic
        );
    }

    public Body callChangeTrack(ChangeTrackDTO dto){
        return kafkaProducer.requestReply(
                dto,
                CoreMessageType.CHANGE_TRACK,
                coreReqTrackTopic
        );
    }

    public Body callDeleteTrack(String trackId){
        return kafkaProducer.requestReply(
                IdDTO.of(trackId),
                CoreMessageType.DELETE_TRACK,
                coreReqTrackTopic
        );
    }
}
