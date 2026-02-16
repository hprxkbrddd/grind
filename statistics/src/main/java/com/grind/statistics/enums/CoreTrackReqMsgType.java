package com.grind.statistics.enums;

import com.grind.statistics.dto.wrap.MessageType;

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
