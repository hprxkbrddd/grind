package com.grind.gateway.service.kafka;

import com.grind.gateway.dto.Body;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stores pending Kafka requests awaiting responses.
 * Uses correlation ids as keys.
 */
@Component
public class PendingRegistry {
    private final ConcurrentHashMap<String, CompletableFuture<Body<?>>> pending = new ConcurrentHashMap<>();

    /**
     * Retrieves a pending future by correlation id.
     *
     * @param correlationId request-reply correlation id
     * @return pending future, or null if missing
     */
    public CompletableFuture<Body<?>> get(String correlationId){
        return pending.get(correlationId);
    }

    /**
     * Registers a pending future by correlation id.
     *
     * @param correlationId request-reply correlation id
     * @param future pending future
     */
    public void put(String correlationId, CompletableFuture<Body<?>> future){
        pending.put(correlationId, future);
    }

    /**
     * Removes and returns a pending future by correlation id.
     *
     * @param correlationId request-reply correlation id
     * @return removed future, or null if missing
     */
    public CompletableFuture<Body<?>> remove(String correlationId){
        return pending.remove(correlationId);
    }
}
