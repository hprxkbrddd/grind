package com.grind.core.enums.coreMessageTypes;

import com.grind.core.dto.wrap.MessageType;

public enum CoreTrackReqMsgType implements MessageType {
    GET_TRACK,
    GET_TRACKS_OF_USER,
    GET_ALL_TRACKS,
    GET_SPRINTS_OF_TRACK,
    CREATE_TRACK,
    CHANGE_TRACK,
    DELETE_TRACK;

    @Override
    public String code() {
        return name();
    }
}
