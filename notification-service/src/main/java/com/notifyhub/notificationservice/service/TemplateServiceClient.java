package com.notifyhub.notificationservice.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class TemplateServiceClient {

    private final WebClient webClient;
    private final TemplateEngine templateEngine;

    @Autowired
    public TemplateServiceClient(WebClient.Builder loadBalancedWebClientBuilder, TemplateEngine templateEngine) {
        this.webClient = loadBalancedWebClientBuilder
                .baseUrl("http://template-service")
                .build();
        this.templateEngine= templateEngine;
    }

    @CircuitBreaker(name="templateService")
    @Retry(name="templateService", fallbackMethod = "fallbackTemplate")
    @TimeLimiter(name="templateService")
    public CompletableFuture<String> fetchTemplate(String tenantId, String eventType, Map<String, Object> payload) {
            return webClient.get()
                    .uri("/api/templates/resolve?tenantId={tenantId}&eventType={eventType}",
                            tenantId, eventType)
                    .retrieve()
                    .bodyToMono(TemplateResponse.class)
                    .map(TemplateResponse::getContent)
                    .toFuture();
    }

    // Called automatically when all retries are exhausted and the circuit doesn't recover
    public CompletableFuture<String> fallbackTemplate(String tenantId, String eventType, Map<String, Object> payload, Throwable t) {
        log.warn("template-service unavailable, using plain HTML fallback. Reason: {}", t.getMessage());
        return CompletableFuture.completedFuture(templateEngine.buildFallbackTemplate(eventType, payload));
    }

    @Data
    private static class TemplateResponse {
        private String content;
        private String eventType;
        private String tenantId;
    }
}
