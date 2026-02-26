package com.grind.core.exception;

public class TaskNotModifiedException extends IllegalArgumentException {
    public TaskNotModifiedException() {
        super("Task was not modified");
    }
}
