package com.grind.gateway.service.kafka;

import com.grind.gateway.dto.Body;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PendingRegistry {
    private final ConcurrentHashMap<String, CompletableFuture<Body<?>>> pending = new ConcurrentHashMap<>();

    public CompletableFuture<Body<?>> get(String correlationId){
        return pending.get(correlationId);
    }

    public void put(String correlationId, CompletableFuture<Body<?>> future){
        pending.put(correlationId, future);
    }

    public CompletableFuture<Body<?>> remove(String correlationId){
        return pending.remove(correlationId);
    }
}
