package com.grind.core.exception;

import jakarta.persistence.EntityNotFoundException;

public class TaskNotFoundException extends EntityNotFoundException {
    public TaskNotFoundException(String taskId) {
        super("There is not task with id:" + taskId);
    }

    public TaskNotFoundException(String taskId, String comment) {
        super(comment + "\nThere is not task with id:" + taskId);
    }
}
