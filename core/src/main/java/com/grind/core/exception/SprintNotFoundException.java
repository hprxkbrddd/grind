package com.grind.core.exception;

import jakarta.persistence.EntityNotFoundException;

public class SprintNotFoundException extends EntityNotFoundException {
    public SprintNotFoundException(String sprintId) {
        super("There is not sprint with id:" + sprintId);
    }

    public SprintNotFoundException(String sprintId, String comment) {
        super(comment + "\nThere is not sprint with id:" + sprintId);
    }
}
