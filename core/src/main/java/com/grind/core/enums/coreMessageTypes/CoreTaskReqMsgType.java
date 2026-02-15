package com.grind.core.enums.coreMessageTypes;

import com.grind.core.dto.wrap.MessageType;

public enum CoreTaskReqMsgType implements MessageType {
    GET_TASKS_OF_TRACK,
    GET_TASKS_OF_SPRINT,
    GET_TASK,
    GET_ALL_TASKS,
    CREATE_TASK,
    CHANGE_TASK,
    PLAN_TASK_DATE,
    PLAN_TASK_SPRINT,
    COMPLETE_TASK,
    MOVE_TASK_TO_BACKLOG,
    DELETE_TASK;

    @Override
    public String code() {
        return name();
    }
}
