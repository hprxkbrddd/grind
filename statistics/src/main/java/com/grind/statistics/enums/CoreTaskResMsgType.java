package com.grind.statistics.enums;

import com.grind.statistics.dto.wrap.MessageType;

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
