package com.grind.core.enums.coreMessageTypes;

import com.grind.core.dto.wrap.MessageType;

public enum CoreTaskResMsgType implements MessageType {
    TASKS_OF_TRACK,
    TASKS_OF_SPRINT,
    TASK,
    ALL_TASKS,
    TASK_CREATED,
    TASK_CHANGED,
    TASK_PLANNED,
    TASK_COMPLETED,
    TASK_AT_BACKLOG,
    TASK_DELETED,
    ;

    @Override
    public String code() {
        return name();
    }
}
