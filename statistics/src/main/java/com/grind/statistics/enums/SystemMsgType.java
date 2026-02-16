package com.grind.statistics.enums;

import com.grind.statistics.dto.wrap.MessageType;

public enum SystemMsgType implements MessageType {
    ERROR,
    UNDEFINED;

    @Override
    public String code() {
        return name();
    }
}
