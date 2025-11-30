package com.grind.gateway.enums;

public enum CoreMessageType {
    // TRACK RELATED

    GET_TRACK,
    CREATE_TRACK,
    CHANGE_TRACK,
    DELETE_TRACK,

    TRACK_CREATED,
    TRACK_DELETED,

    // TASK RELATED

    GET_TASKS_OF_TRACK,
    GET_TASKS_OF_SPRINT,
    GET_TASK,
    CREATE_TASK,
    CHANGE_TASK,
    DELETE_TASK,

    TASK_CREATED,
    TASK_DELETED,
    TASK_CHANGED,

    UNDEFINED
}
