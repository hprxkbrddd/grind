package com.grind.core.enums.coreMessageTypes;

import com.grind.core.dto.wrap.MessageType;

public enum CoreTrackResMsgType implements MessageType {
    TRACKS_OF_USER,
    TRACK,
    ALL_TRACKS,
    SPRINTS_OF_TRACK,
    TRACK_CREATED,
    TRACK_CHANGED,
    TRACK_DELETED;

    @Override
    public String code() {
        return name();
    }
}
