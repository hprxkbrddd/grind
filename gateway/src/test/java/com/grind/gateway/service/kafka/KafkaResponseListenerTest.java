package com.grind.gateway.service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grind.gateway.dto.Body;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KafkaResponseListenerTest {

    @Test
    void handleResponse_shouldCompletePendingFuture() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        PendingRegistry pendingRegistry = new PendingRegistry();
        KafkaResponseListener listener = new KafkaResponseListener(objectMapper, pendingRegistry);

        CompletableFuture<Body<?>> future = new CompletableFuture<>();
        pendingRegistry.put("cid", future);

        listener.handleResponse(
                objectMapper.writeValueAsString(Body.ok("done")),
                "cid"
        );

        assertTrue(future.isDone());
        assertEquals("done", future.join().payload());
        assertNull(pendingRegistry.get("cid"));
    }
}
