package com.grind.core.exception;

import jakarta.persistence.EntityNotFoundException;

public class TrackNotFoundException extends EntityNotFoundException {
    public TrackNotFoundException(String trackId) {
        super("There is not track with id:" + trackId);
    }

    public TrackNotFoundException(String trackId, String comment) {
        super(comment + "\nThere is not track with id:" + trackId);
    }
}
