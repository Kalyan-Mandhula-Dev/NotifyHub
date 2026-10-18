package com.notifyhub.notificationservice.kafka;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PendingEnrichments {

    private final Map<String, CompletableFuture<String>> pending = new ConcurrentHashMap<>();

    public CompletableFuture<String> register(String correlationId) {
        CompletableFuture<String> fut = new CompletableFuture<>();
        pending.put(correlationId, fut);
        return fut;
    }

    public boolean complete(String correlationId, String enrichedTemplate) {
        CompletableFuture<String> fut = pending.remove(correlationId);
        return fut != null && fut.complete(enrichedTemplate);
    }

    public void remove(String correlationId) {
        pending.remove(correlationId);
    }
}
