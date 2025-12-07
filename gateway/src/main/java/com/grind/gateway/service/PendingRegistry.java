package com.grind.gateway.service;

import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PendingRegistry {
    private final ConcurrentHashMap<String, CompletableFuture<Object>> pending = new ConcurrentHashMap<>();

    public CompletableFuture<Object> get(String correlationId){
        return pending.get(correlationId);
    }

    public void put(String correlationId, CompletableFuture<Object> future){
        pending.put(correlationId, future);
    }

    public CompletableFuture<Object> remove(String correlationId){
        return pending.remove(correlationId);
    }
}
