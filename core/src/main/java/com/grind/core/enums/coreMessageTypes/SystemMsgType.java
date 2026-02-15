package com.grind.core.enums.coreMessageTypes;

import com.grind.core.dto.wrap.MessageType;

public enum SystemMsgType implements MessageType {
    ERROR,
    UNDEFINED;

    @Override
    public String code() {
        return name();
    }
}
