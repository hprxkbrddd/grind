package com.grind.core.exception;

public class InvalidAggregateStateException extends RuntimeException {
    public InvalidAggregateStateException(Class<?> aggregateRoot, Class<?> entity) {
        super("Aggregate root ('" + aggregateRoot.getSimpleName() +
                "') has no child entities ('" + entity.getSimpleName() + "')");
    }
}
