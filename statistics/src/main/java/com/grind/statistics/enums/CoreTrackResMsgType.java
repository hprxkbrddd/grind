package com.grind.statistics.enums;

import com.grind.statistics.dto.wrap.MessageType;

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
